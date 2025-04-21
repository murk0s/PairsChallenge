package sia.pairschallenge.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Cacheable(value = "products", key = "#id")
    Optional<Product> findById(Long id);

    @CacheEvict(value = "products", key = "#id")
    void deleteById(Long id);

    @Override
    @CachePut(value = "products", key = "#result.id")
    Product save(Product entity);
}
