package security;

public final class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> holder = new ThreadLocal<>();

    private SecurityContextHolder() {}

    public static SecurityContext getContext() {
        SecurityContext ctx = holder.get();
        if (ctx == null) {
            ctx = new SecurityContext();
            holder.set(ctx);
        }
        return ctx;
    }

    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
    }

    public static Authentication getAuthentication() {
        return getContext().getAuthentication();
    }

    public static void clearContext() {
        holder.remove();
    }
}
