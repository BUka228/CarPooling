package dao.postgres;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class HibernateTestUtil {


    private static volatile SessionFactory sessionFactory = null;
    private static final Object lock = new Object();

    private static SessionFactory buildSessionFactory() {
        try {
            log.info("Building SessionFactory from test configuration (hibernate.cfg.xml)...");
            // Убедитесь, что загружается именно тестовая конфигурация!
            return new Configuration().configure("hibernate.cfg.xml") // Имя тестового файла
                    .buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation for tests failed!", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (lock) {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                    // Добавляем Shutdown Hook для закрытия фабрики при завершении JVM
                    Runtime.getRuntime().addShutdownHook(new Thread(HibernateTestUtil::shutdownInternal));
                }
            }
        }
        if (sessionFactory.isClosed()) {
            log.error("Attempted to get an already closed SessionFactory!");
            // Можно либо бросить исключение, либо попробовать пересоздать (менее предсказуемо)
            throw new IllegalStateException("SessionFactory is closed!");
        }
        return sessionFactory;
    }

    // Метод для вызова из тестов (если нужно явно закрыть) - не используется в @AfterAll
    public static void shutdown() {
        shutdownInternal();
    }

    // Внутренний метод для закрытия (вызывается из Shutdown Hook)
    private static void shutdownInternal() {
        synchronized (lock) { // Синхронизация на случай конкурентного доступа
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                log.info("Shutting down Hibernate SessionFactory (test)...");
                try {
                    sessionFactory.close();
                    log.info("Hibernate SessionFactory (test) shut down.");
                } catch (Exception e) {
                    log.error("Error shutting down SessionFactory.", e);
                } finally {
                    sessionFactory = null; // Сбрасываем ссылку
                }
            }
        }
    }
}
