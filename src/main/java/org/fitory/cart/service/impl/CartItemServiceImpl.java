package org.fitory.cart.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.cart.domain.CartItem;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.cart.exception.CartItemAccessDeniedException;
import org.fitory.cart.exception.CartItemNotFoundException;
import org.fitory.cart.repository.CartItemRepository;
import org.fitory.cart.service.CartItemService;
import org.fitory.cart.dto.CartProductResponse;
import org.fitory.common.dto.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;

    @Override
    public PageResponse<CartProductResponse> findCartProducts(Long userId, int page, int size) {
        List<CartProductResponse> content = cartItemRepository.findCartProductsByUserId(userId, page, size);
        long total = cartItemRepository.countByUserId(userId);
        return PageResponse.of(content, page, size, total);
    }

    @Override
    public CartItemResponse add(AddCartItemRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        CartItem result = cartItemRepository.findByUserIdAndProductId(request.userId(), request.productId())
                .map(existing -> {
                    existing.changeQuantity(existing.getQuantity() + request.quantity());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    return cartItemRepository.save(CartItem.builder()
                            .userId(request.userId())
                            .productId(request.productId())
                            .quantity(request.quantity())
                            .createdAt(now)
                            .updatedAt(now)
                            .build());
                });
        return CartItemResponse.of(result);
    }

    @Override
    public CartItemResponse updateQuantity(Long id, Long userId, UpdateCartItemRequest request) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));
        if (!cartItem.getUserId().equals(userId)) {
            throw new CartItemAccessDeniedException(id);
        }
        cartItem.changeQuantity(request.quantity());
        return CartItemResponse.of(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long id, Long userId) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));
        if (!cartItem.getUserId().equals(userId)) {
            throw new CartItemAccessDeniedException(id);
        }
        cartItemRepository.deleteById(id);
    }

    @Override
    public void deleteAll(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }
}
