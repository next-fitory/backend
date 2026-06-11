package org.fitory.auth.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dashboards")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping
    public ResponseEntity<String> adminOnlyData(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("어드민 권한이 필요합니다.");
        }
        return ResponseEntity.ok("어드민 전용 데이터");
    }
}
