package org.fitory.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TokenResponse(String accessToken, @JsonIgnore String rawRefreshToken) {}
