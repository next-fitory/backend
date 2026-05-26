package org.fitory.product.dto;

import lombok.Builder;
import org.fitory.example.Product;

@Builder
public record RequestDto(Long id, String name, String description, int price) {
    public static RequestDto of(Product product) {
        return RequestDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
