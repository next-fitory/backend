package org.fitory.brand.repository;

import org.fitory.brand.domain.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Brand save(Brand brand);
    Optional<Brand> findById(Long id);
    void deleteById(Long id);
    List<Brand> findAll();
    Optional<Brand> findByName(String name);
}
