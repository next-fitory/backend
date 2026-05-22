package core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class BeanGraph {
    private final Map<String, BeanDefinition> nodeMap = new LinkedHashMap<>();
    private final Map<String, List<String>> edges = new HashMap<>();
    private final Map<String, Integer> indegree = new HashMap<>();

    public BeanGraph(Collection<BeanDefinition> definitions) {
        definitions.forEach(d -> {
            nodeMap.put(d.getName(), d);
            indegree.put(d.getName(), 0);
        });
        buildEdges();
    }

    public List<BeanDefinition> topologicallySorted() {
        List<String> zeros = new ArrayList<>();
        indegree.forEach((name, deg) -> { if (deg == 0) zeros.add(name); });
        Collections.sort(zeros);

        Deque<String> queue = new ArrayDeque<>(zeros);
        List<BeanDefinition> ordered = new ArrayList<>(nodeMap.size());

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            ordered.add(nodeMap.get(cur));
            List<String> dependents = new ArrayList<>(edges.getOrDefault(cur, Collections.emptyList()));
            Collections.sort(dependents);
            for (String next : dependents) {
                if (indegree.merge(next, -1, Integer::sum) == 0) queue.add(next);
            }
        }

        if (ordered.size() != nodeMap.size()) {
            throw new CircularDependencyException("Circular dependency detected among beans");
        }
        return ordered;
    }

    private void buildEdges() {
        for (BeanDefinition def : nodeMap.values()) {
            Class<?>[] rawTypes = def.getConstructorParameterTypes();
            Type[] genericTypes = def.getConstructorGenericParameterTypes();

            for (int i = 0; i < rawTypes.length; i++) {
                if (List.class.isAssignableFrom(rawTypes[i])) {
                    Class<?> elementType = extractListElementType(genericTypes[i]);
                    if (elementType == null) continue;
                    for (String depName : getBeanNamesForType(elementType)) {
                        edges.computeIfAbsent(depName, k -> new ArrayList<>()).add(def.getName());
                        indegree.merge(def.getName(), 1, Integer::sum);
                    }
                } else {
                    List<String> candidates = getBeanNamesForType(rawTypes[i]);
                    if (candidates.isEmpty()) continue;
                    // 인터페이스 등 후보 여러 개면 첫 번째(알파벳순)
                    String depName = candidates.get(0);
                    edges.computeIfAbsent(depName, k -> new ArrayList<>()).add(def.getName());
                    indegree.merge(def.getName(), 1, Integer::sum);
                }
            }
        }
    }

    private List<String> getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (BeanDefinition def : nodeMap.values()) {
            if (type.isAssignableFrom(def.getType())) result.add(def.getName());
        }
        Collections.sort(result);
        return result;
    }

    private static Class<?> extractListElementType(Type genericType) {
        if (genericType instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class<?> c) return c;
        }
        return null;
    }

    public static class CircularDependencyException extends RuntimeException {
        public CircularDependencyException(String message) { super(message); }
    }
}
