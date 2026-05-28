package org.fitory.product.repository;

import org.fitory.product.domain.Product;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.dto.ProductSearchRequest;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findAllByCategoryId(Long categoryId);
    List<Product> findAllByOrderByCreatedAtDesc();
    List<Product> findAllByBrandId(Long brandId, int page, int size);
    long countByBrandId(Long brandId);
    List<ProductResponse> search(ProductSearchRequest request, int page, int size);
    long countSearch(ProductSearchRequest request);
    void deleteById(Long id);
}
