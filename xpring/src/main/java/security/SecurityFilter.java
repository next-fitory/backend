package security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class SecurityFilter implements Filter {

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            Authentication authentication = authenticate(req, resp);
            SecurityContextHolder.setAuthentication(authentication);
            chain.doFilter(request, response);
        } finally {
            // 요청 종료 시 반드시 정리 — 스레드 풀 재사용으로 인한 컨텍스트 누출 방지
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 요청에서 인증 정보를 추출해 반환한다.
     * 인증 불필요한 요청은 null을 반환하면 된다.
     */
    protected abstract Authentication authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException;
}
