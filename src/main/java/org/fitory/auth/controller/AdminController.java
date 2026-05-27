package org.fitory.auth.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;
import security.annotation.CurrentUser;

@RestController
@RequestMapping("/api/admin-dashboards")
@RequiredArgsConstructor
public class AdminController {

    // GET /api/admin-dashboards
    @GetMapping
    public ResponseEntity<String> adminOnlyData(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.unauthorized("인증되지 않은 사용자입니다.");
        }
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.forbidden("어드민 권한이 필요합니다.");
        }
        return ResponseEntity.ok("어드민 전용 데이터");
    }
}