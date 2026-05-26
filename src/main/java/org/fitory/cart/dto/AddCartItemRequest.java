package org.fitory.cart.dto;

public record AddCartItemRequest(Long user_id, Long product_id, int quantity) {
}
