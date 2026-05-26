package org.fitory.review.dto;

public record CreateReviewRequest(Long productId, String content, int rating) {
}
