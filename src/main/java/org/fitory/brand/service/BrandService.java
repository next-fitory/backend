package org.fitory.brand.service;

import org.fitory.brand.dto.BrandResponse;
import org.fitory.brand.dto.CreateBrandRequest;
import org.fitory.brand.dto.UpdateBrandRequest;

import java.util.List;

public interface BrandService {
    List<BrandResponse> findAll();
    BrandResponse findById(Long id);
    BrandResponse create(CreateBrandRequest request);
    BrandResponse update(Long id, UpdateBrandRequest request);
    void delete(Long id);
}
