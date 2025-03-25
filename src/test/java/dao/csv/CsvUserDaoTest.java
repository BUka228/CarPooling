package dao.csv;

import com.carpooling.dao.csv.CsvUserDao;
import com.carpooling.entities.database.Address;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvUserDaoTest {

    private CsvUserDao userDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        String testFileName = "test-users.csv";
        Path filePath = tempDir.resolve(testFileName);
        Files.createFile(filePath);
        tempFile = filePath.toFile();
        userDao = new CsvUserDao(tempFile.getAbsolutePath());
    }

    private User createTestUser() {
        User user = new User();

        user.setName("Test User");
        user.setEmail("test.user." + UUID.randomUUID() + "@example.com");
        user.setPassword("password123");
        user.setGender("Other");
        user.setPhone("123-456-7890");
        user.setBirthDate(new Date(System.currentTimeMillis() - 1000L * 3600 * 24 * 365 * 30));
        user.setPreferences("Non-smoker, Quiet");

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Anytown");
        user.setAddress(address);
        return user;
    }

    @Test
    void createUser_Success() throws DataAccessException {
        User user = createTestUser();
        String id = userDao.createUser(user);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<User> foundUserOpt = userDao.getUserById(id);
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();

        assertEquals(generatedUUID, foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getPassword(), foundUser.getPassword());
        assertEquals(user.getGender(), foundUser.getGender());
        assertEquals(user.getPhone(), foundUser.getPhone());
        assertEquals(user.getPreferences(), foundUser.getPreferences());
        assertNotNull(foundUser.getBirthDate());

        // Проверка Address зависит от того, как opencsv его обработал
        assertNotNull(foundUser.getAddress(), "Address field read from CSV is null. Check opencsv mapping/converter for Address.");
        if (foundUser.getAddress() != null) {
            assertEquals(user.getAddress().getStreet(), foundUser.getAddress().getStreet());
            assertEquals(user.getAddress().getCity(), foundUser.getAddress().getCity());
        }
    }

    @Test
    void createUser_DataAccessException_OnFileError() {
        User user = createTestUser();
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> userDao.createUser(user));
        tempFile.setWritable(true);
    }

    @Test
    void createUser_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userDao.createUser(null));
    }


    @Test
    void getUserById_Success() throws DataAccessException {
        User user = createTestUser();
        String id = userDao.createUser(user);
        Optional<User> foundUser = userDao.getUserById(id);
        assertTrue(foundUser.isPresent());
        assertEquals(UUID.fromString(id), foundUser.get().getId());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void getUserById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<User> foundUser = userDao.getUserById(nonExistentId);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void getUserById_DataAccessException_OnFileError() throws DataAccessException, IOException {
        User user = createTestUser();
        String id = userDao.createUser(user);
        assertTrue(Files.deleteIfExists(tempFile.toPath()));
        assertThrows(DataAccessException.class, () -> userDao.getUserById(id));
    }


    @Test
    void updateUser_Success() throws DataAccessException {
        User user = createTestUser();
        String id = userDao.createUser(user);
        UUID userUUID = UUID.fromString(id);

        User createdUser = userDao.getUserById(id).orElseThrow(() -> new AssertionError("Failed to retrieve user for update test"));

        createdUser.setName("Updated Test User");
        createdUser.setPhone("987-654-3210");
        // Обновляем вложенный объект Address
        if (createdUser.getAddress() == null) createdUser.setAddress(new Address()); // Инициализация, если null
        createdUser.getAddress().setCity("NewCity");

        userDao.updateUser(createdUser); // Используется специфичная реализация CsvUserDao.updateUser

        Optional<User> updatedUserOpt = userDao.getUserById(id);
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();

        assertEquals("Updated Test User", updatedUser.getName());
        assertEquals("987-654-3210", updatedUser.getPhone());
        assertEquals(userUUID, updatedUser.getId());
        assertNotNull(updatedUser.getAddress(), "Address is null after update. Check CSV writing/reading.");
        assertEquals("NewCity", updatedUser.getAddress().getCity(), "Embedded Address city was not updated correctly.");
        // Проверяем, что другие поля адреса не слетели (если они были)
        assertEquals(user.getAddress().getStreet(), updatedUser.getAddress().getStreet());
    }

    @Test
    void updateUser_NotFound() {
        User nonExistentUser = createTestUser();
        nonExistentUser.setId(UUID.randomUUID());
        // Проверяем реализацию CsvUserDao.updateUser
        assertThrows(DataAccessException.class, () -> userDao.updateUser(nonExistentUser));
    }

    @Test
    void updateUser_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userDao.updateUser(null));
    }

    @Test
    void deleteUser_Success() throws DataAccessException {
        User user = createTestUser();
        String id = userDao.createUser(user);
        assertTrue(userDao.getUserById(id).isPresent());
        assertDoesNotThrow(() -> userDao.deleteUser(id));
        assertFalse(userDao.getUserById(id).isPresent());
    }

    @Test
    void deleteUser_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> userDao.deleteUser(nonExistentId));
    }

    @Test
    void deleteUser_DataAccessException_OnFileError() throws DataAccessException {
        User user = createTestUser();
        String id = userDao.createUser(user);
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> userDao.deleteUser(id));
        tempFile.setWritable(true);
    }
}