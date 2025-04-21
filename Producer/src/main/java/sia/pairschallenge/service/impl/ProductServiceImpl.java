package sia.pairschallenge.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import sia.pairschallenge.event.ProductEvent;
import sia.pairschallenge.repository.Product;
import sia.pairschallenge.repository.ProductRepository;
import sia.pairschallenge.service.ProductService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LogManager.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void update(Long id, Product product) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        productRepository.save(existingProduct);
        sendMessage(product, "product updated");
    }

    @Override
    public Product findById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
    }

    @Override
    public void deleteById(Long id) {
        Product productForDelete = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id "+id+" not found"));
        productRepository.deleteById(id);
        sendMessage(productForDelete, "product deleted");
    }

    @Override
    public void create(Product product) {
        productRepository.save(product);
        sendMessage(product, "new product created");
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
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
