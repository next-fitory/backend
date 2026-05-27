package org.fitory.product.dto;

import lombok.Builder;
import org.fitory.product.domain.Product;

@Builder
public record ProductResponse(Long id, String name, int price, int salePrice, int discountRate, String imageUrl, String brandName, int stock) {
    public static ProductResponse of(Product product, String brandName) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .salePrice(product.salePrice())
                .discountRate(product.getDiscountRate())
                .imageUrl(product.getImageUrl())
                .brandName(brandName)
                .stock(product.getStock())
                .build();
    }
}