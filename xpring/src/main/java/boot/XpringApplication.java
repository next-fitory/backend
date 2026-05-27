package boot;

import core.ApplicationContext;
import core.ConfigurationAdapter;
import jakarta.servlet.Filter;
import log.Logger;
import log.XpringLoggerFactory;
import mvc.ExceptionHandlerResolver;
import mvc.HandlerAdapter;
import mvc.HandlerMapping;
import server.tomcat.DispatcherServlet;
import server.tomcat.EmbeddedTomcatServer;

import java.util.List;

public final class XpringApplication {
    private static final Logger log = XpringLoggerFactory.getLogger(XpringApplication.class);

    private static void printBanner() {
        System.out.println();
        System.out.println("  __ _ _                   ");
        System.out.println(" / _(_) |_ ___  _ __ _   _ ");
        System.out.println("| |_| | __/ _ \\| '__| | | |");
        System.out.println("|  _| | || (_) | |  | |_| |");
        System.out.println("|_| |_|\\__\\___/|_|   \\__, |");
        System.out.println("                       |___/ ");
        System.out.println();
    }

    public static ApplicationContext run(Class<?> primarySource) {
        printBanner();

        XpringBootApplication annotation = primarySource.getAnnotation(XpringBootApplication.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    primarySource.getName() + " is not annotated with @XpringBootApplication");
        }

        log.info("Starting Xpring application: {}", primarySource.getSimpleName());

        // 1. .env → application.yml 순으로 로드
        ConfigurationAdapter.loadDotEnv();
        ConfigurationAdapter.loadFromClasspath("application.yml");
        log.info("Loaded application configuration");

        // 2. 빈 컨텍스트 생성
        ApplicationContext context = new ApplicationContext(primarySource.getPackageName());

        // 3. ApplicationRunner 빈 실행
        List<ApplicationRunner> runners = context.getBeansOfType(ApplicationRunner.class);
        if (!runners.isEmpty()) {
            log.info("Running {} ApplicationRunner(s)", runners.size());
        }
        runners.forEach(runner -> {
            log.debug("Running ApplicationRunner: {}", runner.getClass().getSimpleName());
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

        // 5. Filter 빈 수집
        List<Filter> filters = context.getBeansOfType(Filter.class);
        if (!filters.isEmpty()) {
            log.info("Registering {} filter(s)", filters.size());
        }

        // 6. 서버 시작
        try {
            new EmbeddedTomcatServer(dispatcherServlet, filters).start(annotation.port());
        } catch (Exception e) {
            log.error("Failed to start server on port {}", annotation.port());
            throw new RuntimeException("Failed to start embedded server on port " + annotation.port(), e);
        }

        return context;
    }
}
