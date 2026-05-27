package org.fitory.brand.dto;

import lombok.Builder;
import org.fitory.brand.domain.Brand;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record BrandResponse(Long id, String name, String imageUrl, String createdAt, String updatedAt) {
    public static BrandResponse of(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .imageUrl(brand.getImageUrl())
                .createdAt(LocalDateTimeFormatter.date(brand.getCreatedAt()))
                .updatedAt(LocalDateTimeFormatter.date(brand.getUpdatedAt()))
                .build();
    }
}
