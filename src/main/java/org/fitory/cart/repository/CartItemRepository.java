package org.fitory.cart.repository;

import org.fitory.cart.domain.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    CartItem save(CartItem cartItem);
    Optional<CartItem> findById(Long id);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    List<CartItem> findAllByUserId(Long userId);
    void deleteById(Long id);
    void deleteAllByUserId(Long userId);
}
