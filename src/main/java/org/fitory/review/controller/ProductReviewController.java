package org.fitory.review.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.common.dto.PageResponse;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.service.ReviewService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getByProduct(
            @PathVariable Long productId,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(reviewService.findAllByProductId(productId, p, s));
    }
}
