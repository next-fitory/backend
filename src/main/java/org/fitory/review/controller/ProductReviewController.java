package org.fitory.review.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.common.dto.PageResponse;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getByProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(reviewService.findAllByProductId(productId, p, s));
    }
}
