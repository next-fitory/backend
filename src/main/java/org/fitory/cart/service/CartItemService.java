package org.fitory.cart.service;

import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> findAllByUserId(Long userId);
    CartItemResponse add(AddCartItemRequest request);
    CartItemResponse updateQuantity(Long id, UpdateCartItemRequest request);
    void delete(Long id);
    void deleteAll(Long userId);
}
