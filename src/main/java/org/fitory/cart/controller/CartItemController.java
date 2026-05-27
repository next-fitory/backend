package org.fitory.cart.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.cart.dto.AddCartItemRequest;
import org.fitory.cart.dto.CartItemResponse;
import org.fitory.cart.dto.UpdateCartItemRequest;
import org.fitory.cart.service.CartItemService;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@RequestParam(required = true) Long userId) {
        return ResponseEntity.ok(cartItemService.findAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> add(@RequestBody AddCartItemRequest request) {
        return ResponseEntity.created(cartItemService.add(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable Long id,
                                                           @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartItemService.updateQuantity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartItemService.delete(id);
        return ResponseEntity.noContent();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestParam(required = true) Long userId) {
        cartItemService.deleteAll(userId);
        return ResponseEntity.noContent();
    }
}
