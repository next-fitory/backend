package core;

import core.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ComponentScanner {

    public Set<Class<?>> scan(String basePackage) {
        String packagePath = basePackage.replace('.', '/');
        Set<Class<?>> result = new LinkedHashSet<>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = cl.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if ("file".equals(url.getProtocol())) {
                    scanDirectory(new File(url.getFile()), basePackage, result, cl);
                } else if ("jar".equals(url.getProtocol())) {
                    String path = url.getPath();
                    String jarPath = path.substring(5, path.indexOf('!'));
                    scanJar(jarPath, packagePath, result, cl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan package: " + basePackage, e);
        }
        return result;
    }

    private void scanDirectory(File dir, String packageName, Set<Class<?>> result, ClassLoader cl) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), result, cl);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                tryLoad(className, result, cl);
            }
        }
    }

    private void scanJar(String jarPath, String packagePath, Set<Class<?>> result, ClassLoader cl) throws IOException {
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    tryLoad(name.replace('/', '.').replace(".class", ""), result, cl);
                }
            }
        }
    }

    private void tryLoad(String className, Set<Class<?>> result, ClassLoader cl) {
        try {
            Class<?> cls = cl.loadClass(className);
            if (isComponent(cls)) result.add(cls);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
    }

    private boolean isComponent(Class<?> cls) {
        if (cls.isInterface() || cls.isAnnotation() || cls.isEnum()) return false;
        // 1) @Component 직접
        if (cls.isAnnotationPresent(Component.class)) return true;
        // 2) 메타 어노테이션(@Service, @Repository 등)에 @Component가 달려 있는지
        for (Annotation ann : cls.getAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(Component.class)) return true;
        }
        return false;
    }
}
