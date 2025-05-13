package com.carpooling.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.carpooling.constants.Constants.DEFAULT_HIBERNATE_CONFIG_PATH;
import static com.carpooling.constants.Constants.HIBERNATE_CONFIG_PROPERTY;

@Slf4j
public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            String configFile = System.getProperty(HIBERNATE_CONFIG_PROPERTY);

            if (configFile != null && !configFile.isEmpty()) {
                log.info("Loading Hibernate configuration from system property '{}': {}", HIBERNATE_CONFIG_PROPERTY, configFile);
                File customConfigFile = new File(configFile);
                if (customConfigFile.exists()) {
                    configuration.configure(customConfigFile);
                } else {
                    log.warn("Custom Hibernate configuration file specified by system property '{}' not found: {}. Falling back to default.", HIBERNATE_CONFIG_PROPERTY, configFile);
                    configuration.configure(DEFAULT_HIBERNATE_CONFIG_PATH); // Или просто configure() если файл в classpath
                }
            } else {
                log.info("Loading default Hibernate configuration: {}", DEFAULT_HIBERNATE_CONFIG_PATH);
                configuration.configure(DEFAULT_HIBERNATE_CONFIG_PATH); // Загрузка hibernate.cfg.xml из classpath
            }
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Метод для принудительного закрытия (если понадобится)
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            log.info("Shutting down Hibernate SessionFactory...");
            sessionFactory.close();
        }
    }
}