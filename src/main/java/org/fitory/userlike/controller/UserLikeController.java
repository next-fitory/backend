package org.fitory.userlike.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.userlike.dto.CreateUserLikeRequest;
import org.fitory.userlike.dto.UserLikeResponse;
import org.fitory.userlike.service.UserLikeService;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class UserLikeController {
    private final UserLikeService userLikeService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserLikeResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userLikeService.findAllByUserId(userId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<UserLikeResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(userLikeService.findAllByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<UserLikeResponse> like(@RequestBody CreateUserLikeRequest request) {
        return ResponseEntity.created(userLikeService.like(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(@RequestParam(required = true) Long userId, @RequestParam(required = true) Long productId) {
        userLikeService.unlike(userId, productId);
        return ResponseEntity.noContent();
    }
}
