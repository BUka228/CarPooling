package util;

import com.man.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import utils.ConfigurationUtil;
import utils.PostgresConnectionUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
public class PostgresConnectionUtilTest {

    private static final String CONFIG_FILE_PATH = ".src/main/resources/environment.properties"; // Путь к конфиг файлу для тестов

    @BeforeAll
    public static void setUp() {
        // Устанавливаем путь к конфигурационному файлу перед всеми тестами
        System.setProperty("config.file", CONFIG_FILE_PATH);
    }

    @AfterAll
    public static void tearDown() {
        // Убираем свойство, если оно было установлено
        System.clearProperty("config.file");
    }

    @Test
    public void testGetConnection_Success() {
        try (Connection connection = PostgresConnectionUtil.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("SQLException occurred while getting the connection: " + e.getMessage());
        }
    }

    @Test
    public void testGetConnection_InvalidConfig_ThrowsSQLException() {
        // Задаем неверные параметры в конфиге для этого теста
        System.setProperty("config.file", "./src/test/resources/invalid_config.properties");

        SQLException exception = assertThrows(SQLException.class, PostgresConnectionUtil::getConnection, "Expected SQLException to be thrown due to invalid config");

        assertTrue(exception.getMessage().contains("Ошибка загрузки конфигурации базы данных"),
                "Expected exception message to contain the error about configuration loading");
    }

    @Test
    public void testCloseConnection_ConnectionIsClosed() {
        try (Connection connection = PostgresConnectionUtil.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

            // Закрываем соединение явно, проверяя логирование
            PostgresConnectionUtil.closeConnection(connection);

            assertTrue(connection.isClosed(), "Connection should be closed after closeConnection is called");
        } catch (SQLException e) {
            fail("SQLException occurred while closing the connection: " + e.getMessage());
        }
    }

    @Test
    public void testCloseConnection_ConnectionIsAlreadyClosed() {
        try (Connection connection = PostgresConnectionUtil.getConnection()) {
            connection.close();
            assertTrue(connection.isClosed(), "Connection should be closed after close()");

            // Попытка закрыть уже закрытое соединение
            PostgresConnectionUtil.closeConnection(connection);
        } catch (SQLException e) {
            fail("SQLException occurred while closing the connection: " + e.getMessage());
        }
    }
}