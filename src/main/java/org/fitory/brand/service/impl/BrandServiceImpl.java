package org.fitory.brand.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.brand.domain.Brand;
import org.fitory.brand.dto.BrandResponse;
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
    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(BrandResponse::of)
                .toList();
    }

    @Override
    public BrandResponse findById(Long id) {
        return BrandResponse.of(findDomain(id));
    }

    @Override
    public BrandResponse create(CreateBrandRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Brand name must not be blank");
        }
        LocalDateTime now = LocalDateTime.now();
        Brand brand = Brand.builder()
                .name(request.name())
                .imageUrl(request.imageUrl())
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();
        return BrandResponse.of(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, UpdateBrandRequest request) {
        Brand existing = findDomain(id);
        Brand updated = existing.toBuilder()
                .name(request.name() != null ? request.name() : existing.getName())
                .imageUrl(request.imageUrl() != null ? request.imageUrl() : existing.getImageUrl())
                .updatedAt(LocalDateTime.now())
                .build();
        return BrandResponse.of(brandRepository.save(updated));
    }

    @Override
    public void delete(Long id) {
        findDomain(id);
        brandRepository.deleteById(id);
    }

    private Brand findDomain(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
    }
}
