package sia.pairschallenge.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sia.pairschallenge.repository.Product;

public interface ProductService{

    Product update(Long id, Product product);

    Product findById(Long id);

    void deleteById(Long id);

    Product create(Product product);

    Page<Product> findAll(Pageable pageable);

}
