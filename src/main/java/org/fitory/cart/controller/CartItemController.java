package org.fitory.cart.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.CartProductResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.cart.service.CartItemService;
import org.fitory.common.dto.PageResponse;
import security.Authentication;
import security.annotation.CurrentUser;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<PageResponse<CartProductResponse>> getCart(
            @CurrentUser Authentication auth,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Long userId = ((User) auth.getPrincipal()).getId();
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(cartItemService.findCartProducts(userId, p, s));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> add(@RequestBody AddCartItemRequest request, @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.created(cartItemService.add(new AddCartItemRequest(userId, request.productId(), request.quantity())));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable Long id,
                                                           @RequestBody UpdateCartItemRequest request,
                                                           @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.ok(cartItemService.updateQuantity(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        cartItemService.delete(id, userId);
        return ResponseEntity.noContent();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        cartItemService.deleteAll(userId);
        return ResponseEntity.noContent();
    }
}
