package services.xml;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.xml.XmlUserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceXmlTest {

    private UserService userService;

    @BeforeEach
    void setUp(@NotNull @TempDir Path tempDir) throws IOException {
        // Создаем временный XML-файл
        File xmlFile = tempDir.resolve("users.xml").toFile();

        // Инициализируем DAO и сервис
        UserDao userDao = new XmlUserDao(xmlFile.getAbsolutePath());
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void testRegisterUserSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        String userId = userService.registerUser(user);
        assertNotNull(userId);

        Optional<User> foundUser = userService.getUserById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals("Иван Иванов", foundUser.get().getName());
    }

    @Test
    void testRegisterUserFailure() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        assertFalse(userService.registerUser(user).isEmpty());
    }

    @Test
    void testGetUserByIdSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        String userId = userService.registerUser(user);

        Optional<User> foundUser = userService.getUserById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals("Иван Иванов", foundUser.get().getName());
    }

    @Test
    void testGetUserByIdFailure() throws UserServiceException {
        Optional<User> user = userService.getUserById("non-existent-id");
        assertFalse(user.isPresent());
    }

    @Test
    void testUpdateUserSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        String userId = userService.registerUser(user);

        user.setId(userId);
        user.setName("Иван Петров");
        userService.updateUser(user);

        Optional<User> updatedUser = userService.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("Иван Петров", updatedUser.get().getName());
    }

    @Test
    void testUpdateUserFailure() {
        User user = new User();
        user.setId("non-existent-id");
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        assertThrows(UserServiceException.class, () -> userService.updateUser(user));
    }

    @Test
    void testDeleteUserSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        String userId = userService.registerUser(user);
        userService.deleteUser(userId);

        Optional<User> deletedUser = userService.getUserById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testDeleteUserFailure() {
        assertThrows(UserServiceException.class, () -> userService.deleteUser("non-existent-id"));
    }

    @Test
    void testAuthenticateUserSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        userService.registerUser(user);

        assertThrows(UserServiceException.class, () -> userService.authenticateUser("invalid@example.com", "password123"));

    }

    @Test
    void testAuthenticateUserFailure() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        userService.registerUser(user);

        assertThrows(UserServiceException.class, () -> userService.authenticateUser("ivan@example.com", "wrongpassword"));
    }

    @Test
    void testChangePasswordSuccess() throws UserServiceException {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user.setPassword("password123");
        user.setGender("Мужской");
        user.setPhone("1234567890");
        user.setBirthDate(Date.valueOf("1990-01-01"));
        user.setAddress("ул. Пушкина, д.10");
        user.setPreferences("Люблю музыку");

        String userId = userService.registerUser(user);
        userService.changePassword(userId, "newpassword");

        Optional<User> updatedUser = userService.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("newpassword", updatedUser.get().getPassword());
    }

    @Test
    void testChangePasswordFailure() {
        assertThrows(UserServiceException.class, () -> userService.changePassword("non-existent-id", "newpassword"));
    }
}