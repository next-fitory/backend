package org.fitory.example;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.GetMapping;
import mvc.annotation.RequestMapping;
import org.fitory.auth.domain.User;
import security.Authentication;
import security.annotation.CurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    @GetMapping
    public ResponseEntity<String> authTest(@CurrentUser Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(user.getEmail() + " " + user.getRole());
    }
}
