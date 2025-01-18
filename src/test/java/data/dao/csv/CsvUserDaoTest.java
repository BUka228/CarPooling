package data.dao.csv;

import data.model.record.UserRecord;
import data.model.record.UserRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvUserDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private CsvUserDao userDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-users.csv").toFile();
        userDao = new CsvUserDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateUser_Success() {
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("123 Main St");
        userRecord.setPreferences("None");

        String userId = userDao.createUser(userRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(userId);
        assertDoesNotThrow(() -> UUID.fromString(userId));

        // Проверяем, что пользователь был добавлен
        Optional<UserRecord> foundUser = userDao.getUserById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
    }

    @Test
    void testCreateUser_Fail() {
        // Делаем файл недоступным для записи
        tempFile.setReadOnly();

        // Проверяем, что выброшено исключение
        assertThrows(DataAccessException.class, () -> userDao.createUser(new UserRecord()));

        // Восстанавливаем права доступа к файлу
        tempFile.setWritable(true);

    }

    @Test
    void testGetUserById_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("Jane Doe");
        userRecord.setEmail("jane.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Female");
        userRecord.setPhone("0987654321");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("456 Elm St");
        userRecord.setPreferences("None");

        String userId = userDao.createUser(userRecord);

        // Получаем пользователя по ID
        Optional<UserRecord> foundUser = userDao.getUserById(userId);

        // Проверяем, что пользователь найден
        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals("Jane Doe", foundUser.get().getName());
    }

    @Test
    void testGetUserById_NotFound() {
        // Пытаемся получить несуществующего пользователя
        Optional<UserRecord> foundUser = userDao.getUserById("non-existent-id");

        // Проверяем, что пользователь не найден
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("Alice");
        userRecord.setEmail("alice@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Female");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("789 Oak St");
        userRecord.setPreferences("None");

        String userId = userDao.createUser(userRecord);

        // Обновляем пользователя
        userRecord.setName("Alice Smith");
        userDao.updateUser(userRecord);

        // Проверяем, что пользователь обновлен
        Optional<UserRecord> updatedUser = userDao.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("Alice Smith", updatedUser.get().getName());
    }

    @Test
    void testUpdateUser_NotFound() {
        // Пытаемся обновить несуществующего пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setId("non-existent-id");
        userRecord.setName("Bob");

        assertThrows(DataAccessException.class, () -> userDao.updateUser(userRecord));
    }

    @Test
    void testDeleteUser_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("Charlie");
        userRecord.setEmail("charlie@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("101 Pine St");
        userRecord.setPreferences("None");

        String userId = userDao.createUser(userRecord);

        // Удаляем пользователя
        userDao.deleteUser(userId);

        // Проверяем, что пользователь удален
        Optional<UserRecord> deletedUser = userDao.getUserById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testDeleteUser_NotFound() {
        // Пытаемся удалить несуществующего пользователя
        assertThrows(DataAccessException.class, () -> userDao.deleteUser("non-existent-id"));
    }
}