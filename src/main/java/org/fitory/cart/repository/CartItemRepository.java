package org.fitory.cart.repository;

import org.fitory.cart.domain.CartItem;
import org.fitory.cart.dto.CartProductResponse;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    CartItem save(CartItem cartItem);
    Optional<CartItem> findById(Long id);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    List<CartItem> findAllByUserId(Long userId);
    List<CartProductResponse> findCartProductsByUserId(Long userId, int page, int size);
    long countByUserId(Long userId);
    void deleteById(Long id);
    void deleteAllByUserId(Long userId);
}
