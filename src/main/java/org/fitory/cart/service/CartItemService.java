package org.fitory.cart.service;

import org.fitory.cart.domain.CartItem;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.UpdateCartItemRequest;

import java.util.List;

public interface CartItemService {
    List<CartItem> findAllByUserId(Long userId);
    CartItem add(AddCartItemRequest request);
    CartItem updateQuantity(Long id, UpdateCartItemRequest request);
    void delete(Long id);
    void deleteAll(Long userId);
}
