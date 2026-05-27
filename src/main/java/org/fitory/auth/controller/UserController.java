package org.fitory.auth.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.auth.dto.SignupRequest;
import org.fitory.auth.service.AuthService;
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
    public ResponseEntity<String> getMyInfo(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.unauthorized("인증되지 않은 사용자입니다.");
        }
        return ResponseEntity.ok("내 이메일: " + user.getEmail() + ", 권한: " + user.getRole());
    }
}