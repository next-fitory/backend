package org.fitory.auth.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.TokenResponse;
import org.fitory.auth.service.AuthService;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    // POST /api/tokens (로그인)
    @PostMapping
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}