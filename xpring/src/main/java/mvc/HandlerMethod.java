package mvc;

import mvc.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerMethod {
    private final Object controller;
    private final Method method;
    private final HttpMethod httpMethod;
    private final String pathTemplate;
    private final Pattern uriPattern;
    private final List<String> pathVariableNames;

    public HandlerMethod(Object controller, Method method, HttpMethod httpMethod, String pathTemplate) {
        this.controller = controller;
        this.method = method;
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        List<String> varNames = new ArrayList<>();
        this.uriPattern = compileTemplate(pathTemplate, varNames, method.getParameters());
        this.pathVariableNames = Collections.unmodifiableList(varNames);
    }

    // /users/{id}/posts/{postId}  →  regex: ^/users/(\d+)/posts/(\d+)$  (Long/Integer → \d+)
    //                                        ^/items/{slug}             →  ^/items/([^/]+)$
    private static Pattern compileTemplate(String template, List<String> varNames, Parameter[] params) {
        StringBuilder regex = new StringBuilder("^");
        Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(template);
        int last = 0;
        while (m.find()) {
            String varName = m.group(1);
            regex.append(Pattern.quote(template.substring(last, m.start())));
            regex.append(isNumericParam(varName, params) ? "(\\d+)" : "([^/]+)");
            varNames.add(varName);
            last = m.end();
        }
        regex.append(Pattern.quote(template.substring(last)));
        regex.append("$");
        return Pattern.compile(regex.toString());
    }

    private static boolean isNumericParam(String varName, Parameter[] params) {
        for (Parameter param : params) {
            PathVariable pv = param.getAnnotation(PathVariable.class);
            if (pv == null) continue;
            String name = pv.value().isEmpty() ? param.getName() : pv.value();
            if (!varName.equals(name)) continue;
            Class<?> type = param.getType();
            return type == Long.class || type == long.class
                    || type == Integer.class || type == int.class;
        }
        return false;
    }

    public Object getController() { return controller; }
    public Method getMethod() { return method; }
    public HttpMethod getHttpMethod() { return httpMethod; }
    public String getPathTemplate() { return pathTemplate; }
    public Pattern getUriPattern() { return uriPattern; }
    public List<String> getPathVariableNames() { return pathVariableNames; }
}
