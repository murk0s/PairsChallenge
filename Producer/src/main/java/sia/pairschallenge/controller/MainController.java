package sia.pairschallenge.controller;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.pairschallenge.repository.Product;
import sia.pairschallenge.service.impl.ProductServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class MainController {

    private final Logger log = LogManager.getLogger(MainController.class);

    private final ProductServiceImpl productService;

    public MainController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> createNewProduct(@RequestBody Product product) {
        Product newProduct = productService.create(product);
        return ResponseEntity.ok("new product created, id: " + newProduct.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> allProducts = productService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(allProducts.getContent());
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id, @RequestBody Product product) {
        productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }

}
