package data.dao.xml;

import data.dao.base.UserDao;
import data.model.database.User;
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

class XmlUserDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private UserDao userDao;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-users.csv").toFile();
        userDao = new XmlUserDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateUser_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("123 Main St");
        userRecord.setPreferences("None");

        // Создаем пользователя
        String userId = userDao.createUser(userRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(userId);
        assertDoesNotThrow(() -> UUID.fromString(userId));

        // Проверяем, что пользователь был добавлен
        Optional<UserRecord> foundUser = userDao.getUserById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    void testCreateUser_Failure() {
        // Создаем тестового пользователя с некорректными данными (например, null)
        UserRecord userRecord = new UserRecord();
        userRecord.setName(null); // Некорректные данные
        userRecord.setEmail("invalid-email");

        tempFile.setReadOnly();


        // Проверяем, что создание пользователя выбрасывает исключение
        assertThrows(DataAccessException.class, () -> userDao.createUser(userRecord));
    }

    @Test
    void testGetUserById_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("123 Main St");
        userRecord.setPreferences("None");

        // Создаем пользователя и получаем его ID
        String userId = userDao.createUser(userRecord);

        // Получаем пользователя по ID
        Optional<UserRecord> foundUser = userDao.getUserById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
    }

    @Test
    void testGetUserById_NotFound() {
        // Пытаемся получить несуществующего пользователя
        Optional<UserRecord> foundUser = userDao.getUserById("non-existent-id");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("123 Main St");
        userRecord.setPreferences("None");

        // Создаем пользователя и получаем его ID
        String userId = userDao.createUser(userRecord);

        // Обновляем пользователя
        userRecord.setEmail("john.doe.updated@example.com");
        userDao.updateUser(userRecord);

        // Проверяем, что пользователь был обновлен
        Optional<UserRecord> updatedUser = userDao.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("john.doe.updated@example.com", updatedUser.get().getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        // Пытаемся обновить несуществующего пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setId("non-existent-id");
        userRecord.setName("John Doe");

        // Проверяем, что обновление выбрасывает исключение
        assertThrows(DataAccessException.class, () -> userDao.updateUser(userRecord));
    }

    @Test
    void testDeleteUser_Success() {
        // Создаем тестового пользователя
        UserRecord userRecord = new UserRecord();
        userRecord.setName("John Doe");
        userRecord.setEmail("john.doe@example.com");
        userRecord.setPassword("password123");
        userRecord.setGender("Male");
        userRecord.setPhone("1234567890");
        userRecord.setBirthDate(new Date());
        userRecord.setAddress("123 Main St");
        userRecord.setPreferences("None");

        // Создаем пользователя и получаем его ID
        String userId = userDao.createUser(userRecord);

        // Удаляем пользователя
        userDao.deleteUser(userId);

        // Проверяем, что пользователь был удален
        Optional<UserRecord> deletedUser = userDao.getUserById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testDeleteUser_NotFound() {
        // Пытаемся удалить несуществующего пользователя
        assertThrows(DataAccessException.class, () -> userDao.deleteUser("non-existent-id"));
    }
}