package org.fitory.product.service;

import org.fitory.product.dto.ProductResponse;

import java.util.List;

public interface ProductCurationService {
    List<ProductResponse> findNewArrivals();
    List<ProductResponse> findRanked();
}
