package org.fitory.security;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import security.Authentication;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthentication implements Authentication {

    private final User principal;

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Collection<String> getRoles() {
        return List.of(principal.getRole().name());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}