package mvc.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ArgumentResolver;
import mvc.annotation.RequestBody;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Parameter;
import java.util.Map;

public class RequestBodyArgumentResolver implements ArgumentResolver {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> pathVariables) throws Exception {
        JavaType javaType = objectMapper.constructType(parameter.getParameterizedType());
        return objectMapper.readValue(request.getInputStream(), javaType);
    }
}
