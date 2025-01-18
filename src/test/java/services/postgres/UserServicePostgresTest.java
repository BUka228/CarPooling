package services.postgres;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServicePostgresTest extends BasePostgresTest {

    private UserService userService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Создаем подключение к базе данных
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        // Создаем таблицу users
        createUsersTable();

        // Инициализируем DAO и сервис
        UserDao userDao = new PostgresUserDao(connection);
        userService = new UserServiceImpl(userDao);

        // Очищаем таблицу перед каждым тестом
        connection.createStatement().execute("DELETE FROM users");
    }

    private void createUsersTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id UUID PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                gender VARCHAR(50),
                phone VARCHAR(20),
                birth_date DATE,
                address VARCHAR(255),
                preferences TEXT
            )
            """;
        connection.createStatement().execute(sql);
    }

    @Test
    void testRegisterUser() throws UserServiceException {
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
    void testRegisterUserFail() throws UserServiceException {
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

        assertThrows(UserServiceException.class, () -> userService.registerUser(user));
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
    void testGetUserByIdNotFound() throws UserServiceException {
        assertTrue(userService.getUserById(UUID.randomUUID().toString()).isEmpty());
    }

    @Test
    void testUpdateUser() throws UserServiceException {
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
    void testUpdateUserFail() throws UserServiceException {
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

        user.setEmail("another@example.com");


        assertThrows(UserServiceException.class, () -> userService.updateUser(user));
    }


    @Test
    void testDeleteUser() throws UserServiceException {
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
    void testDeleteUserFail() {
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

        assertThrows(UserServiceException.class, () -> userService.authenticateUser("ivan@example.com", "wrongpassword"));
    }


    @Test
    void testAuthenticateUserInvalidCredentials() throws UserServiceException {
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
    void testChangePassword() throws UserServiceException {
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
    void testChangePasswordNotFound() {
        assertThrows(UserServiceException.class, () -> userService.changePassword("non-existent-id", "newpassword"));
    }


    @Test
    void testGetUserByEmail() {
        assertThrows(UserServiceException.class, () -> userService.getUserByEmail("email@example.com"));
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