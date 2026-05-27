package org.fitory.auth.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.TokenResponse;
import org.fitory.auth.service.AuthService;

import java.util.Base64;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    // POST /api/tokens (로그인)
    @PostMapping
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        // 원본 JWT 토큰을 발급
        TokenResponse tokenResponse = authService.login(request);
        String originalAccessToken = tokenResponse.accessToken();

        // 원본 토큰 문자열을 Base64로 인코딩
        String encodedAccessToken = Base64.getEncoder().encodeToString(originalAccessToken.getBytes());

        return ResponseEntity.ok(new TokenResponse(encodedAccessToken));
    }
}