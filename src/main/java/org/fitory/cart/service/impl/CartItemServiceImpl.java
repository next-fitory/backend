package org.fitory.cart.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.cart.domain.CartItem;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.cart.exception.CartItemNotFoundException;
import org.fitory.cart.repository.CartItemRepository;
import org.fitory.cart.service.CartItemService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> findAllByUserId(Long userId) {
        return cartItemRepository.findAllByUserId(userId);
    }

    @Override
    public CartItem add(AddCartItemRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // 동일 상품이 이미 담겨 있으면 수량 합산
        return cartItemRepository.findByUserIdAndProductId(request.user_id(), request.product_id())
                .map(existing -> {
                    existing.changeQuantity(existing.getQuantity() + request.quantity());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    CartItem cartItem = CartItem.builder()
                            .userId(request.user_id())
                            .productId(request.product_id())
                            .quantity(request.quantity())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return cartItemRepository.save(cartItem);
                });
    }

    @Override
    public CartItem updateQuantity(Long id, UpdateCartItemRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));
        cartItem.changeQuantity(request.quantity());
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void delete(Long id) {
        cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));
        cartItemRepository.deleteById(id);
    }

    @Override
    public void deleteAll(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }
}
