package utils;

import com.man.constant.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class PostgresConnectionUtil {
    // Метод для получения соединения
    public static Connection getConnection() throws SQLException {
        try {
            // Получение конфигурации
            String url = ConfigurationUtil.getConfigurationEntry(Constants.DB_URL);
            String user = ConfigurationUtil.getConfigurationEntry(Constants.DB_USER);
            String password = ConfigurationUtil.getConfigurationEntry(Constants.DB_PASSWORD);

            // Проверяем наличие обязательных параметров
            if (url == null || user == null || password == null) {
                log.error("Некорректные параметры подключения: db.url, db.user, или db.password");
                throw new IllegalArgumentException("Параметры подключения к базе данных некорректны или отсутствуют");
            }

            // Возвращаем соединение
            return DriverManager.getConnection(url, user, password);

        } catch (IOException e) {
            log.error("Ошибка загрузки конфигурации базы данных", e);
            throw new SQLException("Ошибка загрузки конфигурации базы данных", e);
        }
    }
    // Метод для закрытия соединения
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    log.info("Соединение с PostgreSQL закрыто.");
                }
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения с PostgreSQL: {}", e.getMessage());
            }
        }
    }
}
