package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonConversionUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonConversionUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // Конвертация JSON в объект
    public static <T> List<T> jsonArrayToObjectList(List<Map<String, Object>> map, Class<T> tClass) {
        try {
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, tClass);
            return objectMapper.convertValue(map, listType);
        } catch (Exception ex) {
            log.error("Не удалось преобразовать JSON в список {}", tClass.getSimpleName(), ex);
            throw new RuntimeException("Ошибка при конвертации JSON", ex);
        }
    }

    // Конвертация объекта в JSON
    public static <T> List<Map<String, Object>> objectListToMapList(List<T> objects) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (T object : objects) {
                // Преобразуем объект в Map
                //TypeReference — это механизм, предоставляемый библиотекой Jackson,
                // который позволяет указать параметризированные типы, такие как Map<String, Object>,
                // в случаях, когда типы не могут быть определены напрямую (например, из-за использования дженериков).
                Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
                mapList.add(map);
            }
            return mapList;
        } catch (Exception ex) {
            log.error("Не удалось преобразовать список объектов в map", ex);
            throw new RuntimeException("Ошибка при преобразовании объекта в map", ex);
        }
    }
}