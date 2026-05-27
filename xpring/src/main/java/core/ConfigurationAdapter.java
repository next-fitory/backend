package core;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationAdapter {
    private static final Map<String, String> properties = new HashMap<>();
    private static final ObjectMapper yamlMapper = new YAMLMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    public static void loadDotEnv() {
        File dotEnv = findDotEnv();
        if (dotEnv == null) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(dotEnv))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx < 0) continue;
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                System.setProperty(key, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static void load(File file) {
        try {
            Map<String, Object> raw = yamlMapper.readValue(file, MAP_TYPE);
            flatten("", raw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from: " + file.getPath(), e);
        }
    }

    public static void loadFromClasspath(String resourceName) {
        try (InputStream is = ConfigurationAdapter.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + resourceName);
            }
            Map<String, Object> raw = yamlMapper.readValue(is, MAP_TYPE);
            flatten("", raw);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from classpath: " + resourceName, e);
        }
    }

    public static String getProperty(String key) {
        if (!properties.containsKey(key)) {
            throw new IllegalArgumentException("Property not found: " + key);
        }
        return properties.get(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public static Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    private static File findDotEnv() {
        File dir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        while (dir != null) {
            File candidate = new File(dir, ".env");
            if (candidate.exists()) return candidate;
            dir = dir.getParentFile();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void flatten(String prefix, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flatten(key, (Map<String, Object>) value);
            } else {
                properties.put(key, value == null ? null : resolveEnvPlaceholder(value.toString()));
            }
        }
    }

    // ${VAR_NAME} 또는 ${VAR_NAME:default} 형태의 환경변수 플레이스홀더 치환
    private static String resolveEnvPlaceholder(String value) {
        if (!value.startsWith("${") || !value.endsWith("}")) {
            return value;
        }
        String inner = value.substring(2, value.length() - 1);
        int colonIdx = inner.indexOf(':');
        String varName = colonIdx >= 0 ? inner.substring(0, colonIdx) : inner;
        String defaultVal = colonIdx >= 0 ? inner.substring(colonIdx + 1) : null;

        String resolved = System.getenv(varName);
        if (resolved != null) return resolved;
        resolved = System.getProperty(varName);
        if (resolved != null) return resolved;
        if (defaultVal != null) return defaultVal;
        throw new IllegalStateException("Environment variable not set and no default provided: " + varName);
    }
}
