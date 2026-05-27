package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;
import mvc.MissingRequestParamException;
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
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        String name = annotation.value();
        if (name.isEmpty()) name = parameter.getName();

        String value = request.getParameter(name);
        if (value == null && annotation.required()) {
            throw new MissingRequestParamException(name);
        }
        return TypeConverter.convert(value, parameter.getType());
    }
}
