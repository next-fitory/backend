package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;
import mvc.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.util.Map;

public class RequestParamArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> pathVariables) {
        String name = parameter.getAnnotation(RequestParam.class).value();
        if (name.isEmpty()) name = parameter.getName();
        return TypeConverter.convert(request.getParameter(name), parameter.getType());
    }
}
