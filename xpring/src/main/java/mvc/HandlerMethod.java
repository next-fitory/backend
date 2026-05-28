package mvc;

import java.lang.reflect.Method;
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
        this.uriPattern = compileTemplate(pathTemplate, varNames);
        this.pathVariableNames = Collections.unmodifiableList(varNames);
    }

    // /users/{id}/posts/{postId}  →  regex: ^/users/([^/]+)/posts/([^/]+)$
    private static Pattern compileTemplate(String template, List<String> varNames) {
        StringBuilder regex = new StringBuilder("^");
        Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(template);
        int last = 0;
        while (m.find()) {
            regex.append(Pattern.quote(template.substring(last, m.start())));
            regex.append("([^/]+)");
            varNames.add(m.group(1));
            last = m.end();
        }
        regex.append(Pattern.quote(template.substring(last)));
        regex.append("$");
        return Pattern.compile(regex.toString());
    }

    public Object getController() { return controller; }
    public Method getMethod() { return method; }
    public HttpMethod getHttpMethod() { return httpMethod; }
    public String getPathTemplate() { return pathTemplate; }
    public Pattern getUriPattern() { return uriPattern; }
    public List<String> getPathVariableNames() { return pathVariableNames; }
}
