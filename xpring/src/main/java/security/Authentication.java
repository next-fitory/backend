package security;

import java.util.Collection;

public interface Authentication {
    Object getPrincipal();
    Collection<String> getRoles();
    boolean isAuthenticated();
}
