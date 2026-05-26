package org.fitory.security;

import java.util.List;

public class SecurityRuleConfig {

    // 인증 없이 접근 가능한 경로 (prefix 매칭)
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/auth"
    );

    // 인증 없이 접근 가능한 (method, exact-path) 쌍
    private static final List<String[]> PUBLIC_EXACT = List.of(
            new String[]{"POST", "/api/users"},   // 회원가입
            new String[]{"GET", "/api/products/*"},
            new String[]{"GET", "/api/brands"},
            new String[]{"GET", "/api/brands/*"},
            new String[]{"GET", "/api/reviews/*"}
    );

    public static boolean isPublic(String method, String uri) {
        for (String prefix : PUBLIC_PREFIXES) {
            if (uri.startsWith(prefix)) return true;
        }
        for (String[] rule : PUBLIC_EXACT) {
            if (rule[0].equalsIgnoreCase(method) && matchesPattern(rule[1], uri)) return true;
        }
        return false;
    }

    // *  → 슬래시 제외 임의 문자열 (단일 세그먼트)
    // ** → 슬래시 포함 임의 문자열 (다중 세그먼트)
    private static boolean matchesPattern(String pattern, String uri) {
        if (!pattern.contains("*")) {
            return pattern.equals(uri);
        }
        String regex = pattern
                .replace(".", "\\.")
                .replace("**", ".+")
                .replace("*", "[^/]+");
        return uri.matches(regex);
    }
}
