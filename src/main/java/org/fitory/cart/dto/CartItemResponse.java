package org.fitory.cart.dto;

import lombok.Builder;
import org.fitory.cart.domain.CartItem;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record CartItemResponse(
        Long id,
        Long userId,
        Long productId,
        int quantity,
        String createdAt,
        String updatedAt
) {
    public static CartItemResponse of(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .userId(cartItem.getUserId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .createdAt(LocalDateTimeFormatter.dateTime(cartItem.getCreatedAt()))
                .updatedAt(LocalDateTimeFormatter.dateTime(cartItem.getUpdatedAt()))
                .build();
    }
}
