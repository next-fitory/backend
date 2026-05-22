package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;
import mvc.annotation.PathVariable;

import java.lang.reflect.Parameter;
import java.util.Map;

public class PathVariableArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> pathVariables) {
        String name = parameter.getAnnotation(PathVariable.class).value();
        if (name.isEmpty()) name = parameter.getName();
        return TypeConverter.convert(pathVariables.get(name), parameter.getType());
    }
}
