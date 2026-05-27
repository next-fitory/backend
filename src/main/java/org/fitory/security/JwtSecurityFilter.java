package org.fitory.security;

import core.annotation.Component;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.auth.repository.UserRepository;
import security.Authentication;
import security.SecurityFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends SecurityFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected Authentication authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authorizationHeader.substring(7);

        if (jwtProvider.validateToken(token)) {
            Claims claims = jwtProvider.parseClaims(token);
            Long userId = Long.parseLong(claims.getSubject());

            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                return new JwtAuthentication(userOpt.get());
            }
        }
        return null;
    }
}