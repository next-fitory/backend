package org.fitory.user.dto;

import lombok.Builder;
import org.fitory.user.domain.User;
import org.fitory.util.LocalDateTimeFormatter;

@Builder
public record UserResponse(Long id, String email, String name, String role, String createdAt) {
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(LocalDateTimeFormatter.dateTime(user.getCreatedAt()))
                .build();
    }

    public static UserResponse of(org.fitory.auth.domain.User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .createdAt(LocalDateTimeFormatter.dateTime(user.getCreatedAt()))
                .build();
    }
}
