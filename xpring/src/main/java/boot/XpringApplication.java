package boot;

import core.ApplicationContext;
import jakarta.servlet.Filter;
import mvc.ExceptionHandlerResolver;
import mvc.HandlerAdapter;
import mvc.HandlerMapping;
import server.tomcat.DispatcherServlet;
import server.tomcat.EmbeddedTomcatServer;

import java.util.List;

public final class XpringApplication {

    public static ApplicationContext run(Class<?> primarySource) {
        XpringBootApplication annotation = primarySource.getAnnotation(XpringBootApplication.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    primarySource.getName() + " is not annotated with @XpringBootApplication");
        }

        // 1. 빈 컨텍스트 생성 (스캔 → 위상정렬 → 인스턴스화)
        ApplicationContext context = new ApplicationContext(primarySource.getPackageName());

        // 2. ApplicationRunner 빈 실행
        context.getBeansOfType(ApplicationRunner.class).forEach(runner -> {
            try {
                runner.run(context);
            } catch (Exception e) {
                throw new RuntimeException("ApplicationRunner failed: " + runner.getClass().getSimpleName(), e);
            }
        });

        // 3. MVC 레이어 구성
        HandlerMapping handlerMapping = new HandlerMapping(context);
        HandlerAdapter handlerAdapter = new HandlerAdapter();
        ExceptionHandlerResolver exceptionHandlerResolver = new ExceptionHandlerResolver(context);

        // 4. DispatcherServlet 조립
        DispatcherServlet dispatcherServlet = new DispatcherServlet(
                handlerMapping, handlerAdapter, exceptionHandlerResolver);

        // 5. Filter 빈 수집 (SecurityFilter 등 jakarta.servlet.Filter 구현체 자동 등록)
        List<Filter> filters = context.getBeansOfType(Filter.class);

        // 6. 서버 시작
        try {
            new EmbeddedTomcatServer(dispatcherServlet, filters).start(annotation.port());
        } catch (Exception e) {
            throw new RuntimeException("Failed to start embedded server on port " + annotation.port(), e);
        }

        return context;
    }
}
