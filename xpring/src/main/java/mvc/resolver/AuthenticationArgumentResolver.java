package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;
import security.Authentication;
import security.SecurityContextHolder;
import security.UnauthorizedException;
import security.annotation.CurrentUser;

import java.lang.reflect.Parameter;
import java.util.Map;

public class AuthenticationArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(CurrentUser.class)
                || Authentication.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> pathVariables) {
        Authentication auth = SecurityContextHolder.getAuthentication();

        // Authentication 타입 직접 주입
        if (Authentication.class.isAssignableFrom(parameter.getType())) {
            return auth;
        }

        // @CurrentUser → principal 반환, 비인증 요청이면 401
        if (auth == null) throw new UnauthorizedException();
        return auth.getPrincipal();
    }
}
