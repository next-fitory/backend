package org.fitory.review.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.common.dto.PageResponse;
import org.fitory.review.dto.CreateReviewRequest;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.dto.UpdateReviewRequest;
import org.fitory.review.service.ReviewService;
import security.Authentication;
import security.annotation.CurrentUser;

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
            @CurrentUser Authentication auth) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.created(reviewService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent();
    }
}
