package core;

import log.Logger;
import log.XpringLoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext implements BeanFactory {
    private static final Logger log = XpringLoggerFactory.getLogger(ApplicationContext.class);

    private final Map<String, Object> beansByName = new LinkedHashMap<>();
    private final Map<Class<?>, List<Object>> beansByType = new LinkedHashMap<>();

    public ApplicationContext(String basePackage) {
        log.info("Scanning components in package: {}", basePackage);
        ComponentScanner scanner = new ComponentScanner();
        Set<Class<?>> classes = scanner.scan(basePackage);
        log.info("Found {} component class(es)", classes.size());

        List<BeanDefinition> definitions = classes.stream()
                .map(BeanDefinition::new)
                .collect(Collectors.toList());

        BeanGraph graph = new BeanGraph(definitions);
        List<BeanDefinition> sorted = graph.topologicallySorted();

        for (BeanDefinition def : sorted) {
            log.debug("Creating bean: {}", def.getName());
            Object bean = createBean(def);
            registerBean(def.getName(), bean);
        }
        log.info("Initialized {} bean(s)", beansByName.size());
    }

    private Object createBean(BeanDefinition def) {
        Constructor<?> ctor = def.getConstructor();
        ctor.setAccessible(true);

        Class<?>[] paramTypes = def.getConstructorParameterTypes();
        Type[] genericTypes = def.getConstructorGenericParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            if (List.class.isAssignableFrom(paramTypes[i])) {
                Class<?> elementType = extractListElementType(genericTypes[i]);
                args[i] = rawBeansOfType(elementType);
            } else {
                args[i] = getBean(paramTypes[i]);
            }
        }

        try {
            return ctor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean: " + def.getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        List<T> candidates = getBeansOfType(clazz);
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    @Override
    public Object getBean(String name) {
        return beansByName.get(name);
    }

    @Override
    public Collection<Object> getBeans() {
        return Collections.unmodifiableCollection(beansByName.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeansOfType(Class<T> clazz) {
        return rawBeansOfType(clazz).stream()
                .map(b -> (T) b)
                .collect(Collectors.toList());
    }

    @Override
    public void registerBean(Object bean) {
        registerBean(decapitalize(bean.getClass().getSimpleName()), bean);
    }

    @Override
    public void registerBean(String name, Object bean) {
        beansByName.put(name, bean);
        for (Class<?> type : collectAllTypes(bean.getClass())) {
            beansByType.computeIfAbsent(type, k -> new ArrayList<>()).add(bean);
        }
    }

    @Override
    public void reset() {
        beansByName.clear();
        beansByType.clear();
    }

    private List<Object> rawBeansOfType(Class<?> type) {
        if (type == null) return Collections.emptyList();
        List<Object> result = new ArrayList<>();
        beansByType.forEach((t, beans) -> {
            if (type.isAssignableFrom(t)) result.addAll(beans);
        });
        return result.stream().distinct().collect(Collectors.toList());
    }

    private static Set<Class<?>> collectAllTypes(Class<?> clazz) {
        Set<Class<?>> types = new LinkedHashSet<>();
        collect(clazz, types);
        return types;
    }

    private static void collect(Class<?> clazz, Set<Class<?>> types) {
        if (clazz == null || clazz == Object.class) return;
        types.add(clazz);
        for (Class<?> iface : clazz.getInterfaces()) collect(iface, types);
        collect(clazz.getSuperclass(), types);
    }

    private static Class<?> extractListElementType(Type genericType) {
        if (genericType instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class<?> c) return c;
        }
        return null;
    }

    private static String decapitalize(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
