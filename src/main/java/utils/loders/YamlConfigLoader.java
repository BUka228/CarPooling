package utils.loders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class YamlConfigLoader implements ConfigLoader {
    @Override
    public Properties load(File file) throws IOException {
        // Создаём ObjectMapper для работы с YAML
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        // Загружаем YAML в Map
        Map yamlMap = yamlMapper.readValue(file, Map.class);
        // Переносим данные из Map в Properties
        Properties properties = new Properties();
        flattenMap("", yamlMap, properties);
        return properties;
    }

    private void flattenMap(String prefix, Map<String, Object> source, Properties target) {
        source.forEach((key, value) -> {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map) {
                // Если значение — вложенный Map, рекурсивно вызываем метод
                flattenMap(fullKey, (Map<String, Object>) value, target);
            } else {
                target.setProperty(fullKey, value.toString());
            }
        });
    }
}
