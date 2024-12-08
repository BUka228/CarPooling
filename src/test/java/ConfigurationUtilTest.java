import com.man.Constants;
import man.TestGener;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationUtilTest extends TestGener {


    @ParameterizedTest
    @ValueSource(strings = {
            "./src/main/resources/environment.properties",
            "./src/main/resources/environment.yml",
            "./src/main/resources/environment.xml"
    })
    void testLoadConfiguration(String configFilePath) throws Exception {
        System.setProperty("config.file", configFilePath);
        ConfigurationUtil.updateConfiguration();

        log.info("Config File: {}", System.getProperty("config.file"));
        log.info("db.url: {}", ConfigurationUtil.getConfigurationEntry("db.url"));
        log.info("db.user: {}", ConfigurationUtil.getConfigurationEntry("db.user"));
        log.info("db.password: {}", ConfigurationUtil.getConfigurationEntry("db.password"));

        assertEquals("jdbc:postgresql://localhost:5432/mydatabase", ConfigurationUtil.getConfigurationEntry(Constants.DB_URL));
        assertEquals("admin", ConfigurationUtil.getConfigurationEntry(Constants.DB_USER));
        assertEquals("password", ConfigurationUtil.getConfigurationEntry(Constants.DB_PASSWORD));
    }

    @Test
    void testGetConfigList() throws IOException{
        System.setProperty("config.file", "./src/main/resources/environment.properties");
        log.info(System.getProperty("config.file"));

        String planetsString = ConfigurationUtil.getConfigurationEntry("planets");
        List<String> expectedList = Arrays.asList("Земля", "Сатурн", "Марс", "Венера");
        List<String> actualList = planetsString != null ? Arrays.asList(planetsString.split(",")) : null;
        assert actualList != null;
        log.info(actualList.toString());

        assertEquals(expectedList, actualList);
    }
    @Test
    void testGetIntStringMap() throws IOException {
        System.setProperty("config.file", "./src/main/resources/environment.yml");
        log.info(System.getProperty("config.file"));


        String monthsString = ConfigurationUtil.getConfigurationEntry("months");

        Map<Integer, String> expectedMap = getIntegerStringMap();


        Map<Integer, String> actualMap = new HashMap<>();
        if (monthsString != null) {
            String[] entries = monthsString.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        int intKey = Integer.parseInt(parts[0].trim());
                        actualMap.put(intKey, parts[1].trim());
                    } catch (NumberFormatException e) {
                        log.info(parts[0]);
                        log.info("Error parsing int key: {}", e.getMessage());
                    }
                }
            }
        }
        log.info(actualMap.toString());

        assertEquals(expectedMap, actualMap);
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
