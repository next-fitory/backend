package org.fitory.brand.service;

import org.fitory.brand.domain.Brand;
import org.fitory.brand.dto.CreateBrandRequest;
import org.fitory.brand.dto.UpdateBrandRequest;

import java.util.List;

public interface BrandService {
    List<Brand> findAll();
    Brand findById(Long id);
    Brand create(CreateBrandRequest request);
    Brand update(Long id, UpdateBrandRequest request);
    void delete(Long id);
}
