package org.fitory.auth.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.TokenResponse;
import org.fitory.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        TokenResponse tokenResponse = authService.login(request);
        String originalAccessToken = tokenResponse.accessToken();

        String encodedAccessToken = Base64.getEncoder().encodeToString(originalAccessToken.getBytes());

        return ResponseEntity.ok(new TokenResponse(encodedAccessToken));
    }
}
