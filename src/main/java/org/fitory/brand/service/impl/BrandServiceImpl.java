package org.fitory.brand.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.brand.domain.Brand;
import org.fitory.brand.dto.CreateBrandRequest;
import org.fitory.brand.dto.UpdateBrandRequest;
import org.fitory.brand.exception.BrandNotFoundException;
import org.fitory.brand.repository.BrandRepository;
import org.fitory.brand.service.BrandService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
    }

    @Override
    public Brand create(CreateBrandRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Brand name must not be blank");
        }
        LocalDateTime now = LocalDateTime.now();
        Brand brand = Brand.builder()
                .name(request.name())
                .imageUrl(request.image_url())
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();
        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, UpdateBrandRequest request) {
        Brand existing = findById(id);
        Brand updated = existing.toBuilder()
                .name(request.name() != null ? request.name() : existing.getName())
                .imageUrl(request.image_url() != null ? request.image_url() : existing.getImageUrl())
                .updatedAt(LocalDateTime.now())
                .build();
        return brandRepository.save(updated);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        brandRepository.deleteById(id);
    }
}
