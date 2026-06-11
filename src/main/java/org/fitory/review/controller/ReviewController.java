package org.fitory.review.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.common.dto.PageResponse;
import org.fitory.review.dto.CreateReviewRequest;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.dto.UpdateReviewRequest;
import org.fitory.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponse>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(reviewService.findAll(p, s));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        return ResponseEntity.ok(reviewService.update(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        reviewService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
