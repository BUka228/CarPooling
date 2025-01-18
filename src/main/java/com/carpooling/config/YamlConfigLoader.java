package com.carpooling.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static com.carpooling.constants.ErrorMessages.ERROR_LOADING_YAML;
import static com.carpooling.constants.LogMessages.FAILED_TO_LOAD_YAML;

public class YamlConfigLoader extends ConfigLoader {

    @Override
    public Properties load(File file) throws IOException {
        Yaml yaml = new Yaml();
        Properties props = new Properties();

        try (InputStream input = createFileInputStream(file)) {
            // Загружаем YAML в Map
            Map<String, Object> yamlData = yaml.load(input);

            // Преобразуем Map в Properties
            flattenMap("", yamlData, props);
        } catch (YAMLException e) {
            log.error(FAILED_TO_LOAD_YAML, file.getAbsolutePath(), e);
            throw new IOException(ERROR_LOADING_YAML + file, e);
        }

        return props;
    }

    /**
     * Рекурсивно преобразует вложенную Map в плоскую структуру Properties.
     *
     * @param prefix Префикс для ключей (используется для вложенных структур).
     * @param source Исходная Map.
     * @param target Целевой объект Properties.
     */
    private void flattenMap(String prefix, Map<?, ?> source, Properties target) {
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Если значение — это вложенная Map, рекурсивно обрабатываем её
                flattenMap(key, (Map<?, ?>) value, target);
            } else if (value instanceof Iterable) {
                // Если значение — это список, преобразуем его в строку
                target.setProperty(key, iterableToString((Iterable<?>) value));
            } else if (value instanceof Map && isNumberedMap((Map<?, ?>) value)) {
                // Если это нумерованный список (Map<Integer, String>), преобразуем его в строку
                target.setProperty(key, mapToString((Map<?, ?>) value));
            } else {
                // Иначе просто добавляем значение как строку
                target.setProperty(key, String.valueOf(value));
            }
        }
    }

    /**
     * Проверяет, является ли Map нумерованным списком (ключи — числа).
     *
     * @param map Исходная Map.
     * @return true, если ключи — числа, иначе false.
     */
    private boolean isNumberedMap(Map<?, ?> map) {
        return map.keySet().stream().allMatch(k -> k instanceof Integer);
    }

    /**
     * Преобразует Iterable (например, List) в строку с разделителями.
     *
     * @param iterable Исходный Iterable.
     * @return Строка, содержащая элементы Iterable, разделённые запятыми.
     */
    private String iterableToString(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder();
        for (Object item : iterable) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }
            sb.append(String.valueOf(item)); // Преобразуем каждый элемент в строку
        }
        return sb.toString();
    }

    /**
     * Преобразует Map<Integer, String> в строку в формате "ключ:значение,ключ:значение,...".
     *
     * @param map Исходная Map.
     * @return Строка, содержащая все пары ключ-значение, разделённые запятыми.
     */
    private String mapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }
}