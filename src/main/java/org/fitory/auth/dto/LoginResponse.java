package org.fitory.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponse(Long userId, String accessToken, @JsonIgnore String rawRefreshToken) {}
