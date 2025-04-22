package sia.pairschallenge.service;

import sia.pairschallenge.repository.Product;

import java.util.List;

public interface ProductService{

    Product update(Long id, Product product);

    Product findById(Long id);

    void deleteById(Long id);

    Product create(Product product);

    List<Product> findAll();

}
