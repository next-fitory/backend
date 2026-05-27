package mvc;

import mvc.resolver.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class HandlerAdapter {
    private final List<ArgumentResolver> argumentResolvers;

    public HandlerAdapter() {
        this.argumentResolvers = List.of(
                new AuthenticationArgumentResolver(),
                new PathVariableArgumentResolver(),
                new RequestParamArgumentResolver(),
                new RequestBodyArgumentResolver(),
                new HttpServletRequestArgumentResolver(),
                new HttpServletResponseArgumentResolver()
        );
    }

    public HandlerAdapter(List<ArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public void handle(HttpServletRequest req, HttpServletResponse resp,
                       HandlerExecution execution) throws Exception {
        HandlerMethod handlerMethod = execution.handler();
        Map<String, String> pathVars = execution.pathVariables();

        Method method = handlerMethod.getMethod();
        Object[] args = resolveArguments(method, req, resp, pathVars);
        Object result = method.invoke(handlerMethod.getController(), args);

        ResponseWriter.write(result, resp);
    }

    private Object[] resolveArguments(Method method, HttpServletRequest req, HttpServletResponse resp,
                                       Map<String, String> pathVars) throws Exception {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            ArgumentResolver resolver = argumentResolvers.stream()
                    .filter(r -> r.supports(param))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No ArgumentResolver found for parameter: " + param.getName()
                                    + " (" + param.getType().getSimpleName() + ")"));
            args[i] = resolver.resolve(param, req, resp, pathVars);
        }
        return args;
    }
}
