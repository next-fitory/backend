package org.fitory.product.dto;

import lombok.Builder;
import org.fitory.product.domain.Product;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record ProductResponse(Long id, Long brandId, Long categoryId, String name, String description,
                              int price, int salePrice, int discountRate, int stock, String imageUrl, String brandName,
                              String createAt, String updatedAt) {
    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .brandId(product.getBrandId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.salePrice())
                .discountRate(product.getDiscountRate())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .brandName("")
                .createAt(LocalDateTimeFormatter.date(product.getCreatedAt()))
                .updatedAt(LocalDateTimeFormatter.date(product.getUpdatedAt()))
                .build();
    }
}