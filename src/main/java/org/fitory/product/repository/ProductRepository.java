package org.fitory.product.repository;

import org.fitory.product.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findAllByCategoryId(Long categoryId);
    List<Product> findAllByOrderByCreatedAtDesc();
    void deleteById(Long id);
}
