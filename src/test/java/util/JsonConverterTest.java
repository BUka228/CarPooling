package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class JsonConverterTest{

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonArrayToObjectList_success() throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map1 = Map.of("name", "Test1", "value", 1);
        Map<String, Object> map2 = Map.of("name", "Test2", "value", 2);
        mapList.add(map1);
        mapList.add(map2);
        log.info(mapList.toString());

        List<TestClass> result = jsonArrayToObjectList(mapList, TestClass.class);
        log.info(result.get(0).getName());

        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals(1, result.get(0).getValue());
        assertEquals("Test2", result.get(1).getName());
        assertEquals(2, result.get(1).getValue());
    }


    @Test
    public void testJsonArrayToObjectList_emptyInput() {
        List<TestClass> result = jsonArrayToObjectList(new ArrayList<>(), TestClass.class);
        assertTrue(result.isEmpty());
    }



    @Test
    public void testJsonArrayToObjectList_exception() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(Map.of("invalidField", "value"));


        assertThrows(RuntimeException.class, () -> jsonArrayToObjectList(mapList, TestClass.class));

        log.error("Не удалось преобразовать JSON в список {} {}",
                TestClass.class.getSimpleName(), Exception.class);

    }

    @Test
    public void testObjectListToMapList_success() {
        List<TestClass> objectList = new ArrayList<>();
        objectList.add(new TestClass("Test1", 1));
        objectList.add(new TestClass("Test2", 2));
        log.info("Object List: {}", objectList);


        List<Map<String, Object>> result = objectListToMapList(objectList);
        log.info("Map List: {}", result.toString());


        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).get("name"));
        assertEquals(1, result.get(0).get("value"));
        assertEquals("Test2", result.get(1).get("name"));
        assertEquals(2, result.get(1).get("value"));
    }

    @Test
    public void testObjectListToMapList_emptyInput() {
        List<Map<String, Object>> result = objectListToMapList(new ArrayList<>());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testObjectListToMapList_exception() {
    }




    public static class TestClass {
        private String name;
        private int value;

        public TestClass() {}

        public TestClass(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
        @Override
        public String toString() {
            return "TestClass{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }



    public  <T> List<T> jsonArrayToObjectList(List<Map<String, Object>> map, Class<T> tClass) {
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, tClass);
            return objectMapper.convertValue(map, listType);
        } catch (Exception ex) {
            log.error("Не удалось преобразовать JSON в список {}", tClass.getSimpleName(), ex);
            throw new RuntimeException("Ошибка при конвертации JSON", ex);
        }
    }

    public  <T> List<Map<String, Object>> objectListToMapList(List<T> objects) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            for (T object : objects) {
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
