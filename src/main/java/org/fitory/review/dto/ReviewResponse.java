package org.fitory.review.dto;

import lombok.Builder;
import org.fitory.review.domain.Review;
import org.fitory.user.dto.UserResponse;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record ReviewResponse(
        Long id,
        Long productId,
        int rating,
        String content,
        UserResponse user,
        String createdAt,
        String updatedAt
) {
    public static ReviewResponse of(Review review, UserResponse user) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .rating(review.getRating())
                .content(review.getContent())
                .user(user)
                .createdAt(LocalDateTimeFormatter.dateTime(review.getCreatedAt()))
                .updatedAt(LocalDateTimeFormatter.dateTime(review.getUpdatedAt()))
                .build();
    }
}
