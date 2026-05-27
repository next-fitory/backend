package org.fitory.product.repository;

import org.fitory.product.dto.ProductResponse;

import java.util.List;

public interface ProductCurationRepository {
    List<ProductResponse> findNewArrivals();
    List<ProductResponse> findRanked();
}
