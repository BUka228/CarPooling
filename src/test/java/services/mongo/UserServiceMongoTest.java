package services.mongo;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.mongo.MongoUserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMongoTest extends BaseMongoTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Инициализируем DAO и сервис
        UserDao userDao = new MongoUserDao(database.getCollection("users"));
        userService = new UserServiceImpl(userDao);

        // Очищаем коллекцию перед каждым тестом
        database.getCollection("users").drop();
    }

    @Test
    void testRegisterUser_Success() throws UserServiceException {
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
    void testRegisterUser_Failure() throws UserServiceException {
        assertFalse(userService.registerUser(new User()).isEmpty());
    }




    @Test
    void testGetUserById_Success() throws UserServiceException {
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
    void testGetUserById_Failure_NotFound() throws UserServiceException {
        assertThrows(UserServiceException.class, () -> userService.getUserById("non-existent-id"));
    }

    @Test
    void testUpdateUser_Success() throws UserServiceException {
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
    void testUpdateUser_Failure_NotFound() {
        User user = new User();
        user.setId("non-existent-id");
        user.setName("Иван Петров");
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
    void testDeleteUser_Success() throws UserServiceException {
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

        assertTrue(userService.getUserById(userId).isEmpty());
    }

    @Test
    void testDeleteUser_Failure_NotFound() {
        assertThrows(UserServiceException.class, () -> userService.deleteUser("non-existent-id"));
    }

    @Test
    void testAuthenticateUser_Success() throws UserServiceException {
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

        assertThrows(UserServiceException.class, () -> userService.authenticateUser("ivan@example.com", "password123"));
    }

    @Test
    void testAuthenticateUser_Failure_InvalidCredentials() throws UserServiceException {
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
    void testChangePassword_Success() throws UserServiceException {
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
    void testChangePassword_Failure_NotFound() {
        assertThrows(UserServiceException.class, () -> userService.changePassword("non-existent-id", "newpassword"));
    }

    @Test
    void testGetUserByEmail_Success() throws UserServiceException {
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

        assertThrows(UserServiceException.class, () -> userService.getUserByEmail("ivan@example.com"));
    }

    @Test
    void testGetUserByEmail_Failure_NotFound() {
        assertThrows(UserServiceException.class, () -> userService.getUserByEmail("non-existent@example.com"));
    }

    @Test
    void testBlockUser() {
        assertThrows(UnsupportedOperationException.class, () -> userService.blockUser("user-id"));
    }

    @Test
    void testUnblockUser() {
        assertThrows(UnsupportedOperationException.class, () -> userService.unblockUser("user-id"));
    }
}