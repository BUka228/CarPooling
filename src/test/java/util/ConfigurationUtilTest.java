package util;

import com.man.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import utils.ConfigurationUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ConfigurationUtilTest {


    @ParameterizedTest
    @ValueSource(strings = {
            "./src/main/resources/environment.properties",
            "./src/main/resources/environment.yaml",
            "./src/main/resources/environment.xml"
    })
    void testLoadConfiguration(String configFilePath) throws Exception {
        System.setProperty("config.file", configFilePath);
        ConfigurationUtil.updateConfiguration();

        log.info("Config File: {}", System.getProperty("config.file"));
        log.info("db.url: {}", ConfigurationUtil.getConfigurationEntry("db.url"));
        log.info("db.user: {}", ConfigurationUtil.getConfigurationEntry("db.user"));
        log.info("db.password: {}", ConfigurationUtil.getConfigurationEntry("db.password"));

        assertEquals("jdbc:postgresql://192.168.56.1:5432/CarPooling", ConfigurationUtil.getConfigurationEntry(Constants.DB_URL));
        assertEquals("postgres", ConfigurationUtil.getConfigurationEntry(Constants.DB_USER));
        assertEquals("11111111", ConfigurationUtil.getConfigurationEntry(Constants.DB_PASSWORD));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "./src/main/resources/environment.properties",
            "./src/main/resources/environment.yaml",
            "./src/main/resources/environment.xml"
    })
    void testGetConfigList(String configFilePath) throws IOException{
        System.setProperty("config.file", configFilePath);
        log.info(System.getProperty("config.file"));

        String planetsString = ConfigurationUtil.getConfigurationEntry("planets");
        List<String> expectedList = Arrays.asList("Земля", "Сатурн", "Марс", "Венера");
        List<String> actualList = planetsString != null ? Arrays.asList(planetsString.split(",")) : null;
        assert actualList != null;
        log.info(actualList.toString());

        assertEquals(expectedList, actualList);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "./src/main/resources/environment.properties",
            "./src/main/resources/environment.yaml",
            "./src/main/resources/environment.xml"
    })
    void testGetIntStringMap(String configFilePath) throws IOException {
        // Устанавливаем путь к конфигурационному файлу
        System.setProperty("config.file", configFilePath);
        log.info("Config file: {}", System.getProperty("config.file"));

        // Получаем строку с месяцами из конфигурации
        String monthsString = ConfigurationUtil.getConfigurationEntry("months");
        log.info("Months string from config: {}", monthsString);

        // Проверяем, что строка не пустая
        assertNotNull(monthsString, "Months string should not be null");

        // Ожидаемый результат
        Map<Integer, String> expectedMap = getIntegerStringMap();

        // Разбираем строку на пары ключ-значение
        Map<Integer, String> actualMap = new HashMap<>();
        String[] entries = monthsString.split(",");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                try {
                    int intKey = Integer.parseInt(parts[0].trim());
                    actualMap.put(intKey, parts[1].trim());
                } catch (NumberFormatException e) {
                    log.error("Error parsing int key: {}", parts[0], e);
                    fail("Failed to parse key: " + parts[0]);
                }
            } else {
                log.error("Invalid entry format: {}", entry);
                fail("Invalid entry format: " + entry);
            }
        }

        log.info("Actual map: {}", actualMap);

        // Проверяем, что actualMap соответствует expectedMap
        assertEquals(expectedMap, actualMap, "The parsed map does not match the expected map");
    }

    @NotNull
    private static Map<Integer, String> getIntegerStringMap() {
        Map<Integer, String> expectedMap = new HashMap<>();
        expectedMap.put(1, "Январь");
        expectedMap.put(2, "Февраль");
        expectedMap.put(3, "Март");
        expectedMap.put(4, "Апрель");
        expectedMap.put(5, "Май");
        expectedMap.put(6, "Июнь");
        expectedMap.put(7, "Июль");
        expectedMap.put(8, "Август");
        expectedMap.put(9, "Сентябрь");
        expectedMap.put(10, "Октябрь");
        expectedMap.put(11, "Ноябрь");
        expectedMap.put(12, "Декабрь");
        return expectedMap;
    }
}
