package org.fitory.brand.repository.impl;

import lombok.RequiredArgsConstructor;
import org.fitory.brand.domain.Brand;
import org.fitory.brand.repository.BrandRepository;
import org.fitory.brand.repository.impl.mapper.BrandMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisBrandRepository implements BrandRepository {

    private final BrandMapper brandMapper;

    @Override
    public Brand save(Brand brand) {
        if (brand.getId() == null) {
            return brandMapper.insert(brand);
        }
        return brandMapper.update(brand);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandMapper.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        brandMapper.softDeleteById(id, LocalDateTime.now());
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.findAll();
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return brandMapper.findByName(name);
    }
}
