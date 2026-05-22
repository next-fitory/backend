package boot;

import core.ApplicationContext;

@FunctionalInterface
public interface ApplicationRunner {
    void run(ApplicationContext context) throws Exception;
}
