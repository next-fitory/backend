package org.fitory.product.service;

import org.fitory.product.dto.CreateProductRequest;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll(Long categoryId, String sort);

    ProductResponse findById(Long id);

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void delete(Long id);
}
