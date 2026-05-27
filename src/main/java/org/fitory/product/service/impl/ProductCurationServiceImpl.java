package org.fitory.product.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.repository.ProductCurationRepository;
import org.fitory.product.service.ProductCurationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCurationServiceImpl implements ProductCurationService {
    private final ProductCurationRepository productCurationRepository;

    @Override
    public List<ProductResponse> findNewArrivals() {
        return productCurationRepository.findNewArrivals();
    }

    @Override
    public List<ProductResponse> findRanked() {
        return productCurationRepository.findRanked();
    }
}
