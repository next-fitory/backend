package org.fitory.userlike.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserLike {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;
}
