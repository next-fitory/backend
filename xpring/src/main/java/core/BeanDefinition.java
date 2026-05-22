package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class BeanDefinition {
    private final Class<?> type;
    private final String name;
    private final Constructor<?> constructor;

    public BeanDefinition(Class<?> type) {
        this.type = type;
        this.name = decapitalize(type.getSimpleName());
        this.constructor = resolveConstructor(type);
    }

    private static Constructor<?> resolveConstructor(Class<?> type) {
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        if (ctors.length == 1) return ctors[0];
        Constructor<?> best = ctors[0];
        for (Constructor<?> c : ctors) {
            if (c.getParameterCount() > best.getParameterCount()) best = c;
        }
        return best;
    }

    private static String decapitalize(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public Class<?> getType() { return type; }
    public String getName() { return name; }
    public Constructor<?> getConstructor() { return constructor; }
    public Class<?>[] getConstructorParameterTypes() { return constructor.getParameterTypes(); }
    public Type[] getConstructorGenericParameterTypes() { return constructor.getGenericParameterTypes(); }
}
