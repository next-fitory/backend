package org.fitory.cart.dto;

import lombok.Builder;
import org.fitory.cart.domain.CartItem;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record CartItemResponse(
        Long id,
        Long user_id,
        Long product_id,
        int quantity,
        String created_at,
        String updated_at
) {
    public static CartItemResponse of(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .user_id(cartItem.getUserId())
                .product_id(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .created_at(LocalDateTimeFormatter.dateTime(cartItem.getCreatedAt()))
                .updated_at(LocalDateTimeFormatter.dateTime(cartItem.getUpdatedAt()))
                .build();
    }
}
