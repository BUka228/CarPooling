package dao.userdao;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractUserDaoTest {

    protected UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = createUserDao();
    }

    @AfterEach
    public void tearDown() {
        cleanUp();
    }

    protected abstract UserDao createUserDao();
    protected abstract void cleanUp();

    @Test
    void createUser_successful() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        String userId = userDao.createUser(user);

        assertNotNull(userId, "Идентификатор пользователя не должен быть null");
        Optional<User> createdUser = userDao.getUserById(userId);
        assertTrue(createdUser.isPresent(), "Пользователь должен быть создан");
        assertEquals("John Doe", createdUser.get().getName(), "Имя пользователя должно совпадать");
    }

    @Test
    void createUser_withInvalidData_throwsException() {
        User user = new User();
        user.setName(null); // Invalid name (null)
        user.setEmail("invalid-email"); // Invalid email
        user.setPassword("short"); // Password too short

        assertThrows(DataAccessException.class, () -> userDao.createUser(user),
                "Должно выброситься исключение при создании пользователя с некорректными данными");
    }

    @Test
    void getUserById_successful() {
        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("password123");
        String userId = userDao.createUser(user);

        Optional<User> retrievedUser = userDao.getUserById(userId);

        assertTrue(retrievedUser.isPresent(), "Пользователь должен быть найден");
        assertEquals("Jane Doe", retrievedUser.get().getName(), "Имя пользователя должно совпадать");
    }

    @Test
    void getUserById_withNonExistingId_returnsEmpty() {
        String nonExistingId = "non-existing-id";

        Optional<User> retrievedUser = userDao.getUserById(nonExistingId);

        assertFalse(retrievedUser.isPresent(), "Не должно быть пользователя с несуществующим ID");
    }

    @Test
    void updateUser_successful() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("password123");
        String userId = userDao.createUser(user);

        User updatedUser = userDao.getUserById(userId).get();
        updatedUser.setName("Alice Smith");
        userDao.updateUser(updatedUser);

        Optional<User> retrievedUser = userDao.getUserById(userId);
        assertTrue(retrievedUser.isPresent(), "Пользователь должен существовать после обновления");
        assertEquals("Alice Smith", retrievedUser.get().getName(), "Имя пользователя должно быть обновлено");
    }

    @Test
    void updateUser_withNonExistingUser_throwsException() {
        User nonExistingUser = new User();
        nonExistingUser.setId(UUID.fromString("non-existing-id"));
        nonExistingUser.setName("Bob");
        nonExistingUser.setEmail("bob@example.com");
        nonExistingUser.setPassword("password123");

        assertThrows(DataAccessException.class, () -> userDao.updateUser(nonExistingUser),
                "Должно выброситься исключение при обновлении несуществующего пользователя");
    }

    @Test
    void deleteUser_successful() {
        User user = new User();
        user.setName("Charlie");
        user.setEmail("charlie@example.com");
        user.setPassword("password123");
        String userId = userDao.createUser(user);

        userDao.deleteUser(userId);

        Optional<User> retrievedUser = userDao.getUserById(userId);
        assertFalse(retrievedUser.isPresent(), "Пользователь должен быть удален");
    }

    @Test
    void deleteUser_withNonExistingId_doesNotThrowException() {
        String nonExistingId = "non-existing-id";

        assertDoesNotThrow(() -> userDao.deleteUser(nonExistingId),
                "Удаление несуществующего пользователя не должно выбрасывать исключение");
    }
}