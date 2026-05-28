package org.fitory.product.service;

import org.fitory.common.dto.PageResponse;
import org.fitory.product.dto.CreateProductRequest;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.dto.ProductSearchRequest;
import org.fitory.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();
    PageResponse<ProductResponse> findAllByBrandId(Long brandId, int page, int size);
    PageResponse<ProductResponse> search(ProductSearchRequest request, int page, int size);

    ProductResponse findById(Long id);

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void delete(Long id);
}
