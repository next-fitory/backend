package org.fitory.auth.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.auth.dto.SignupRequest;
import org.fitory.auth.service.AuthService;
import org.fitory.user.dto.UserResponse;
import security.annotation.CurrentUser;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    // POST /api/users (회원가입)
    @PostMapping
    public ResponseEntity<User> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.created(authService.signup(request));
    }

    // GET /api/users/me (내 정보 조회)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@CurrentUser User user) {
        return ResponseEntity.ok(UserResponse.of(user));
    }
}