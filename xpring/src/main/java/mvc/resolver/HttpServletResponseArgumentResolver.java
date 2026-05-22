package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;

import java.lang.reflect.Parameter;
import java.util.Map;

public class HttpServletResponseArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return HttpServletResponse.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> pathVariables) {
        return response;
    }
}
