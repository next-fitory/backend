package org.fitory.cart.dto;

public record AddCartItemRequest(Long userId, Long productId, int quantity) {
}
