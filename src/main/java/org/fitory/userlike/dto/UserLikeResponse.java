package org.fitory.userlike.dto;

import lombok.Builder;
import org.fitory.userlike.domain.UserLike;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record UserLikeResponse(
        Long id,
        Long userId,
        Long productId,
        String createdAt
) {
    public static UserLikeResponse of(UserLike userLike) {
        return UserLikeResponse.builder()
                .id(userLike.getId())
                .userId(userLike.getUserId())
                .productId(userLike.getProductId())
                .createdAt(LocalDateTimeFormatter.dateTime(userLike.getCreatedAt()))
                .build();
    }
}
