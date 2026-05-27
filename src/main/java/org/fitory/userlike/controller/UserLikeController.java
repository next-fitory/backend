package org.fitory.userlike.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.userlike.dto.CreateUserLikeRequest;
import org.fitory.userlike.dto.UserLikeResponse;
import org.fitory.userlike.service.UserLikeService;
import security.Authentication;
import security.annotation.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class UserLikeController {
    private final UserLikeService userLikeService;

    @GetMapping
    public ResponseEntity<List<UserLikeResponse>> getByUser(@CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.ok(userLikeService.findAllByUserId(userId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<UserLikeResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(userLikeService.findAllByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<UserLikeResponse> like(@RequestBody CreateUserLikeRequest request, @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.created(userLikeService.like(new CreateUserLikeRequest(userId, request.productId())));
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(@RequestParam(required = true) Long productId, @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        userLikeService.unlike(userId, productId);
        return ResponseEntity.noContent();
    }
}
