package sia.pairschallenge.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sia.pairschallenge.event.ProductEvent;
import sia.pairschallenge.repository.Product;
import sia.pairschallenge.repository.ProductRepository;
import sia.pairschallenge.service.ProductService;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LogManager.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, ProductEvent> kafkaTemplate, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#result.id")
    public Product update(Long id, Product product) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct = productRepository.save(existingProduct);
        entityManager.flush();
        sendMessage(existingProduct, "product updated");
        return existingProduct;
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteById(Long id) {
        Product productForDelete = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
        productRepository.deleteById(id);
        sendMessage(productForDelete, "product deleted");
    }

    @Override
    @CachePut(value = "products", key = "#result.id")
    public Product create(Product product) {
        product = productRepository.save(product);
        sendMessage(product, "new product created");
        return product;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    private void sendMessage(Product product, String message) {
        CompletableFuture<SendResult<String, ProductEvent>> send = kafkaTemplate.send("product-events",
                new ProductEvent(message,
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()));
        send.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("message failed to send", exception);
            }
        });
    }
}
