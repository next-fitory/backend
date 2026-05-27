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
        // path variable이 없는 exact 라우트를 먼저 탐색하도록 정렬
        // ex) GET /products/ranked 가 GET /products/{id} 보다 우선
        handlers.sort(Comparator.comparingInt(h -> h.getPathVariableNames().size()));
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

        String uri = req.getRequestURI();
        for (HandlerMethod handler : handlers) {
            if (handler.getHttpMethod() != httpMethod) continue;
            Matcher m = handler.getUriPattern().matcher(uri);
            if (m.matches()) {
                Map<String, String> pathVars = new LinkedHashMap<>();
                List<String> names = handler.getPathVariableNames();
                for (int i = 0; i < names.size(); i++) {
                    pathVars.put(names.get(i), m.group(i + 1));
                }
                HandlerExecution execution = new HandlerExecution(handler, pathVars);
                // path variable이 없는 라우트만 캐싱 — 가변 URI를 키로 쓰면 캐시가 무한 증가함
                if (names.isEmpty()) {
                    routeCache.put(cacheKey, execution);
                }
                return Optional.of(execution);
            }
        }
        return Optional.empty();
    }

    private static boolean isRestController(Class<?> cls) {
        if (cls.isAnnotationPresent(RestController.class)) return true;
        for (Annotation ann : cls.getAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(RestController.class)) return true;
        }
        return false;
    }
}
