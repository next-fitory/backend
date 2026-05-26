package org.fitory.brand.dto;

import lombok.Builder;
import org.fitory.brand.domain.Brand;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record CreateBrandResponse(Long id, String name, String image_url, String created_at, String updated_at) {
    public static CreateBrandResponse of(Brand brand) {
        return CreateBrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image_url(brand.getImageUrl())
                .created_at(LocalDateTimeFormatter.date(brand.getCreatedAt()))
                .updated_at(LocalDateTimeFormatter.date(brand.getUpdatedAt()))
                .build();
    }
}
