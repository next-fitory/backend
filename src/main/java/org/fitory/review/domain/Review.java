package org.fitory.review.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Review {
    private Long id;
    private Long productId;
    private Long userId;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
