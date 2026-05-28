package mvc;

import core.BeanFactory;
import core.annotation.RestController;
import log.Logger;
import log.XpringLoggerFactory;
import mvc.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

public class HandlerMapping {
    private static final Logger log = XpringLoggerFactory.getLogger(HandlerMapping.class);

    private final List<HandlerMethod> handlers = new ArrayList<>();
    private final Map<String, HandlerExecution> routeCache = new ConcurrentHashMap<>();

    public HandlerMapping(BeanFactory beanFactory) {
        beanFactory.getBeans().stream()
                .filter(bean -> isRestController(bean.getClass()))
                .forEach(this::registerController);
        // path variable 개수 오름차순 → 정적 세그먼트가 많은(구체적인) 경로를 먼저 매칭
        // 같은 개수면 경로 길이 내림차순 → /products/{id}/comments가 /products/{id}보다 우선
        handlers.sort(Comparator
                .comparingInt((HandlerMethod h) -> h.getPathVariableNames().size())
                .thenComparingInt(h -> -h.getPathTemplate().length()));
    }

    private void registerController(Object controller) {
        Class<?> cls = controller.getClass();
        String classPath = cls.isAnnotationPresent(RequestMapping.class)
                ? cls.getAnnotation(RequestMapping.class).value()
                : "";

        for (Method method : cls.getDeclaredMethods()) {
            extractMapping(controller, method, classPath);
        }
    }

    private void extractMapping(Object controller, Method method, String classPath) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            register(controller, method, HttpMethod.GET,
                    classPath + method.getAnnotation(GetMapping.class).value());
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            register(controller, method, HttpMethod.POST,
                    classPath + method.getAnnotation(PostMapping.class).value());
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            register(controller, method, HttpMethod.PUT,
                    classPath + method.getAnnotation(PutMapping.class).value());
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            register(controller, method, HttpMethod.DELETE,
                    classPath + method.getAnnotation(DeleteMapping.class).value());
        } else if (method.isAnnotationPresent(PatchMapping.class)) {
            register(controller, method, HttpMethod.PATCH,
                    classPath + method.getAnnotation(PatchMapping.class).value());
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            register(controller, method, rm.method(), classPath + rm.value());
        }
    }

    private void register(Object controller, Method method, HttpMethod httpMethod, String path) {
        method.setAccessible(true);
        handlers.add(new HandlerMethod(controller, method, httpMethod, path));
        log.info("Mapped {} {} -> {}.{}()",
                httpMethod, path,
                controller.getClass().getSimpleName(), method.getName());
    }

    public Optional<HandlerExecution> getHandler(HttpServletRequest req) {
        String cacheKey = req.getMethod() + ":" + req.getRequestURI();

        HandlerExecution cached = routeCache.get(cacheKey);
        if (cached != null) return Optional.of(cached);

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(req.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        // 첫 번째 매칭이 아닌 가장 구체적인(path variable 최소) 핸들러를 선택
        // handlers는 정렬되어 있으므로 0-variable 매칭 즉시 break 가능 (성능)
        // 정렬이 환경에 따라 보장되지 않을 수 있으므로 정확성은 여기서 담보
        String uri = req.getRequestURI();
        HandlerExecution bestMatch = null;
        int bestVarCount = Integer.MAX_VALUE;

        for (HandlerMethod handler : handlers) {
            if (handler.getHttpMethod() != httpMethod) continue;
            List<String> names = handler.getPathVariableNames();
            if (names.size() >= bestVarCount) continue;

            Matcher m = handler.getUriPattern().matcher(uri);
            if (m.matches()) {
                Map<String, String> pathVars = new LinkedHashMap<>();
                for (int i = 0; i < names.size(); i++) {
                    pathVars.put(names.get(i), m.group(i + 1));
                }
                bestMatch = new HandlerExecution(handler, pathVars);
                bestVarCount = names.size();
                if (bestVarCount == 0) break;
            }
        }

        if (bestMatch == null) return Optional.empty();
        if (bestMatch.handler().getPathVariableNames().isEmpty()) {
            routeCache.put(cacheKey, bestMatch);
        }
        return Optional.of(bestMatch);
    }

    private static boolean isRestController(Class<?> cls) {
        if (cls.isAnnotationPresent(RestController.class)) return true;
        for (Annotation ann : cls.getAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(RestController.class)) return true;
        }
        return false;
    }
}
