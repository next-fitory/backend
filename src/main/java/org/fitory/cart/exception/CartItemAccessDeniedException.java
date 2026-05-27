package org.fitory.cart.exception;

public class CartItemAccessDeniedException extends RuntimeException {
    public CartItemAccessDeniedException(Long id) {
        super("Access denied to cart item with id: " + id);
    }
}
