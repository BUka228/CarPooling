package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.loders.ConfigLoader;
import utils.loders.PropertiesConfigLoader;
import utils.loders.XmlConfigLoader;
import utils.loders.YamlConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class ConfigurationUtil {

    private static final String DEFAULT_CONFIG_PATH = "./src/main/resources/environment.properties";
    private static  Properties configuration = new Properties();
    private static final Logger log = LoggerFactory.getLogger(ConfigurationUtil.class);

    public ConfigurationUtil() throws IOException {}

    private static Properties getConfiguration() throws IOException {
        if(configuration.isEmpty()){
            loadConfiguration();
        }
        return configuration;
    }

    /**
     * Loads configuration from <code>DEFAULT_CONFIG_PATH</code>
     * @throws IOException In case of the configuration file read failure
     */
    private static void loadConfiguration() throws IOException {
        String configFile = System.getProperty("config.file", DEFAULT_CONFIG_PATH);
        File nf = new File(configFile);
        String extension = getFileExtension(nf.getName());
        ConfigLoader loader = getLoaderForExtension(extension);
        configuration = loader.load(nf);
    }

    /**
     * Gets configuration entry value
     * @param key Entry key
     * @return Entry value by key
     * @throws IOException In case of the configuration file read failure
     */
    public static String getConfigurationEntry(String key) throws IOException {
        return getConfiguration().getProperty(key);
    }

    private static ConfigLoader getLoaderForExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "properties":
                return new PropertiesConfigLoader();
            case "yml":
            case "yaml":
                return new YamlConfigLoader();
            case "xml":
                return new XmlConfigLoader();
            default:
                throw new IllegalArgumentException("Unsupported file format: " + extension);
        }
    }
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /*public static List<String> getStringList(String key) throws IOException {
        String value = getConfigurationEntry(key);
        if (value == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(value.split(","));
    }

    public static Map<Integer, String> getIntStringMap(String key) throws IOException {
        String value = getConfigurationEntry(key);
        if (value == null) {
            return Collections.emptyMap();
        }
        Map<Integer, String> map = new HashMap<>();
        String[] entries = value.split(",");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                try {
                    int intKey = Integer.parseInt(parts[0].trim());
                    map.put(intKey, parts[1].trim());
                } catch (NumberFormatException e) {
                    // Обработка ошибки преобразования
                    log.info("Invalid integer key in configuration: {}", entry);
                }
            }
        }
        return map;
    }*/
}
