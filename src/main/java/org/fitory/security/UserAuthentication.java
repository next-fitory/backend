package org.fitory.security;

import security.Authentication;

import java.util.Collection;
import java.util.List;

public class UserAuthentication implements Authentication {
    private final Long userId;
    private final String email;

    public UserAuthentication(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Collection<String> getRoles() {
        return List.of("ROLE_USER");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
