package org.fitory.cart.service;

import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.common.dto.PageResponse;
import org.fitory.product.dto.ProductResponse;

public interface CartItemService {
    PageResponse<ProductResponse> findCartProducts(Long userId, int page, int size);
    CartItemResponse add(AddCartItemRequest request);
    CartItemResponse updateQuantity(Long id, Long userId, UpdateCartItemRequest request);
    void delete(Long id, Long userId);
    void deleteAll(Long userId);
}
