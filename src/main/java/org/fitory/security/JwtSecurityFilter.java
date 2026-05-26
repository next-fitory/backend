package org.fitory.security;

import core.annotation.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.service.JwtProvider;
import security.SecurityContextHolder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilter implements Filter {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        try {
            if (SecurityRuleConfig.isPublic(req.getMethod(), req.getRequestURI())) {
                chain.doFilter(req, resp);
                return;
            }

            String token = extractBearerToken(req);
            if (token == null || !jwtProvider.isValid(token)) {
                writeUnauthorized(resp);
                return;
            }

            Long userId = jwtProvider.getUserId(token);
            String email = jwtProvider.getEmail(token);
            SecurityContextHolder.setAuthentication(new UserAuthentication(userId, email));
            chain.doFilter(req, resp);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String extractBearerToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required\"}");
    }
}
