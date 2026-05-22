package log;

class Slf4jLogger implements Logger {
    private final org.slf4j.Logger delegate;

    Slf4jLogger(Class<?> clazz) {
        this.delegate = org.slf4j.LoggerFactory.getLogger(clazz);
    }

    @Override public void info(String message, Object... args)  { delegate.info(message, args); }
    @Override public void warn(String message, Object... args)  { delegate.warn(message, args); }
    @Override public void debug(String message, Object... args) { delegate.debug(message, args); }
    @Override public void error(String message, Object... args) { delegate.error(message, args); }
    @Override public void error(String message, Throwable cause){ delegate.error(message, cause); }
}
