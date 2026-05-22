package log;

public final class XpringLoggerFactory {
    private XpringLoggerFactory() {}

    public static Logger getLogger(Class<?> clazz) {
        return new Slf4jLogger(clazz);
    }
}
