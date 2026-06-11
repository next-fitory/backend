package org.fitory.cart.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.CartProductResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.cart.service.CartItemService;
import org.fitory.common.dto.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<PageResponse<CartProductResponse>> getCart(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Long userId = user.getId();
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(cartItemService.findCartProducts(userId, p, s));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> add(@RequestBody AddCartItemRequest request, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.add(new AddCartItemRequest(userId, request.productId(), request.quantity())));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable Long id,
                                                           @RequestBody UpdateCartItemRequest request,
                                                           @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.ok(cartItemService.updateQuantity(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        cartItemService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        cartItemService.deleteAll(userId);
        return ResponseEntity.noContent().build();
    }
}
