package org.fitory.security;

import org.fitory.auth.domain.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final User principal;

    public JwtAuthentication(User principal) {
        super(List.of(new SimpleGrantedAuthority("ROLE_" + principal.getRole().name())));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public User getPrincipal() {
        return principal;
    }
}
