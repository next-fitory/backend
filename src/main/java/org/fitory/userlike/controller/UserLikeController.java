package org.fitory.userlike.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.userlike.dto.CreateUserLikeRequest;
import org.fitory.userlike.dto.UserLikeResponse;
import org.fitory.userlike.service.UserLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class UserLikeController {
    private final UserLikeService userLikeService;

    @GetMapping
    public ResponseEntity<List<UserLikeResponse>> getByUser(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.ok(userLikeService.findAllByUserId(userId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<UserLikeResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(userLikeService.findAllByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<UserLikeResponse> like(@RequestBody CreateUserLikeRequest request, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(userLikeService.like(new CreateUserLikeRequest(userId, request.productId())));
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(@RequestParam Long productId, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userLikeService.unlike(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
