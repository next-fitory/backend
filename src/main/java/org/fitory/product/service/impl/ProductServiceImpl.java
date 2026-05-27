package org.fitory.product.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.example.ProductNotFoundException;
import org.fitory.product.domain.Product;
import org.fitory.product.dto.CreateProductRequest;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.dto.UpdateProductRequest;
import org.fitory.product.repository.ProductRepository;
import org.fitory.product.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> findAll(Long categoryId, String sort) {
        if (categoryId != null) {
            return productRepository.findAllByCategoryId(categoryId).stream()
                    .map(ProductResponse::of)
                    .toList();
        }

        if ("newest".equals(sort)) {
            return productRepository.findAllByOrderByCreatedAtDesc().stream()
                    .map(ProductResponse::of)
                    .toList();
        }

        return productRepository.findAll().stream()
                .map(ProductResponse::of)
                .toList();
    }

    @Override
    public ProductResponse findById(Long id) {
        return ProductResponse.of(findDomain(id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        
        return ProductResponse.of(productRepository.save(Product.create(request)));
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product existing = findDomain(id);
        Product updated = existing.update(request);

        return ProductResponse.of(productRepository.save(updated));
    }

    @Override
    public void delete(Long id) {
        findDomain(id);
        productRepository.deleteById(id);
    }

    private Product findDomain(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return product;
    }
}