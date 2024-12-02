package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.loders.ConfigLoader;
import utils.loders.PropertiesConfigLoader;
import utils.loders.XmlConfigLoader;
import utils.loders.YamlConfigLoader;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


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
        if (!nf.exists()) {
            log.error("Configuration file not found: {}", configFile);
            throw new IOException("Configuration file not found: " + configFile);
        }
        try {
            String extension = getFileExtension(nf.getName());
            ConfigLoader loader = getLoaderForExtension(extension);
            configuration = loader.load(nf);
        } catch (IOException e) {
            log.error("Failed to load configuration from {}", configFile, e);
            throw new IOException("Error loading configuration file: " + configFile, e);
        }
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

    private static ConfigLoader getLoaderForExtension(String extension) throws IllegalArgumentException {
        return switch (extension.toLowerCase()) {
            case "properties" -> new PropertiesConfigLoader();
            case "yml", "yaml" -> new YamlConfigLoader();
            case "xml" -> new XmlConfigLoader();
            default -> {
                log.error("Unsupported file format: {}", extension);
                throw new IllegalArgumentException("Unsupported file format: " + extension);
            }
        };
    }
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        //Обнаруженное расширение файла
        log.debug("Detected file extension: {}", extension);
        return extension;
    }

    public static void updateConfiguration() throws IllegalStateException {
        try {
            loadConfiguration();
        } catch (IOException e) {
            log.error("Failed to load configuration", e);
            throw new IllegalStateException("Configuration update failed", e);
        }
    }
}
