package mvc;

import core.BeanFactory;
import mvc.annotation.ControllerAdvice;
import mvc.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionHandlerResolver {
    private final Map<Class<? extends Throwable>, HandlerEntry> handlers = new LinkedHashMap<>();

    public ExceptionHandlerResolver(BeanFactory beanFactory) {
        beanFactory.getBeans().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(ControllerAdvice.class))
                .forEach(this::registerAdvice);
    }

    private void registerAdvice(Object advice) {
        for (Method method : advice.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ExceptionHandler.class)) continue;
            method.setAccessible(true);
            for (Class<? extends Throwable> exType : method.getAnnotation(ExceptionHandler.class).value()) {
                handlers.put(exType, new HandlerEntry(advice, method));
            }
        }
    }

    /**
     * @return 처리됐으면 true, 핸들러 없으면 false
     */
    public boolean resolve(Exception exception, HttpServletRequest req, HttpServletResponse resp) {
        HandlerEntry entry = findEntry(exception.getClass());
        if (entry == null) return false;

        try {
            Object result = invoke(entry, exception, req, resp);
            ResponseWriter.write(result, resp);
            return true;
        } catch (Exception invokeEx) {
            throw new RuntimeException("ExceptionHandler execution failed", invokeEx);
        }
    }

    private HandlerEntry findEntry(Class<?> exType) {
        // 예외 클래스 계층을 올라가며 가장 구체적인 핸들러 탐색
        while (exType != null && exType != Object.class) {
            HandlerEntry entry = handlers.get(exType);
            if (entry != null) return entry;
            exType = exType.getSuperclass();
        }
        return null;
    }

    private Object invoke(HandlerEntry entry, Exception exception,
                          HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Parameter[] params = entry.method().getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            if (Throwable.class.isAssignableFrom(type)) {
                args[i] = exception;
            } else if (HttpServletRequest.class.isAssignableFrom(type)) {
                args[i] = req;
            } else if (HttpServletResponse.class.isAssignableFrom(type)) {
                args[i] = resp;
            }
        }
        return entry.method().invoke(entry.instance(), args);
    }

    private record HandlerEntry(Object instance, Method method) {}
}
