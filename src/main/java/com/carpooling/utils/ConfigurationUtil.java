package com.carpooling.utils;


import com.carpooling.config.ConfigLoader;
import com.carpooling.config.PropertiesConfigLoader;
import com.carpooling.config.XmlConfigLoader;
import com.carpooling.config.YamlConfigLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static com.carpooling.constants.Constants.DEFAULT_CONFIG_PATH;


@Slf4j
public class ConfigurationUtil {
    private static  Properties configuration = new Properties();

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
            log.error("Файл конфигурации не найден: {}", configFile);
            throw new IOException("Файл конфигурации не найден: " + configFile);
        }
        try {
            String extension = getFileExtension(nf.getName());
            ConfigLoader loader = getLoaderForExtension(extension);
            configuration = loader.load(nf);
        } catch (IOException e) {
            log.error("Не удалось загрузить конфигурацию из {}", configFile, e);
            throw new IOException("Ошибка загрузки файла конфигурации: " + configFile, e);
        }
    }

    /**
     * Gets configuration entry value
     * @param key Entry key
     * @return Entry value by key
     * @throws IOException In case of the configuration file read failure
     */
    public static String getConfigurationEntry(String key) throws IOException {
        try {
            return getConfiguration().getProperty(key);
        } catch (IOException e) {
            log.error("Ошибка чтения ко ключу конфигурации {} {}", key, e);
            throw new IOException("Ошибка чтения ко ключу конфигурации", e);
        }
    }

    private static ConfigLoader getLoaderForExtension(String extension) throws IllegalArgumentException {
        return switch (extension.toLowerCase()) {
            case "properties" -> new PropertiesConfigLoader();
            case "yml", "yaml" -> new YamlConfigLoader();
            case "xml" -> new XmlConfigLoader();
            default -> {
                log.error("Неподдерживаемый формат файла: {}", extension);
                throw new IllegalArgumentException("Неподдерживаемый формат файла: " + extension);
            }
        };
    }
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static void updateConfiguration() throws IllegalStateException {
        try {
            loadConfiguration();
        } catch (IOException e) {
            log.error("Не удалось загрузить конфигурацию", e);
            throw new IllegalStateException("Обновление конфигурации не удалось", e);
        }
    }
}
