package org.fitory.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.auth.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (jwtProvider.isExpired(token)) {
            request.setAttribute("TOKEN_EXPIRED", true);
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtProvider.validateToken(token)) {
            Claims claims = jwtProvider.parseClaims(token);
            Long userId = Long.parseLong(claims.getSubject());
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(userOpt.get()));
            }
        }

        filterChain.doFilter(request, response);
    }
}
