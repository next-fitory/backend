package org.fitory.product.service;

import org.fitory.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();
}
