package dao.postgres;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.Address;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.hibernate.ThreadLocalSessionContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;



class PostgresUserDaoTest {

    private static SessionFactory sessionFactory;
    private UserDao userDao;
    private Session session;
    private Transaction transaction;

    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() {}

    @BeforeEach
    void setUp() {
        userDao = new PostgresUserDao(sessionFactory);
        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) { System.err.println("Error rolling back: " + e.getMessage()); }
        finally {
            try { ThreadLocalSessionContext.unbind(); }
            catch (Exception e) { System.err.println("Error unbinding: " + e.getMessage()); }
            finally {
                if (session != null && session.isOpen()) { session.close(); }
            }
        }
    }

    // --- Хелперы ---
    private User buildUser(String email) {
        User user = new User();
        user.setName("Test " + (email != null ? email : "NoEmail"));
        user.setEmail(email);
        user.setPassword("password");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAddress(new Address("Street", "123", "City"));
        return user;
    }

    private User createAndPersistUser(String email) throws DataAccessException {
        User user = buildUser(email);
        // Сохраняем через DAO в транзакции теста
        userDao.createUser(user);
        session.flush();
        session.clear();
        // Возвращаем объект с ID, но он detached из-за clear()
        // Для дальнейших манипуляций в том же тесте лучше загрузить его снова
        // Но для Arrange этого достаточно
        return user; // Возвращаем ID
    }

    // ================== Тесты createUser ==================

    @Test
    void createUser_Success_ShouldPersistAndReturnId() throws DataAccessException {
        // Arrange
        User user = buildUser("create.success@test.com");

        // Act
        String userIdStr = userDao.createUser(user);
        UUID generatedId = user.getId(); // Получаем ID из объекта
        session.flush(); // Применяем

        // Assert
        assertThat(generatedId).isNotNull();
        assertThat(userIdStr).isEqualTo(generatedId.toString());

        session.clear(); // Очищаем для чистоты проверки
        User foundUser = session.get(User.class, generatedId);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("create.success@test.com");
    }

    @Test
    void createUser_Failure_NullEmail_ShouldThrowDataAccessException() { // Ожидаем DataAccessException
        // Arrange
        User user = buildUser(null);

        // Act & Assert: Ожидаем DataAccessException, т.к. DAO оборачивает ошибку persist
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.createUser(user); // Ошибка PropertyValueException происходит внутри DAO
            // session.flush(); // Flush не нужен, ошибка уже произошла
        });

        // Проверяем причину
        assertThat(ex.getCause()).isInstanceOf(PersistenceException.class);
        Throwable rootCause = ex.getCause();
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("User.email");
    }

    @Test
    void createUser_Failure_DuplicateEmail_ShouldThrowDataAccessException() throws DataAccessException {
        // Arrange
        String email = "duplicate.email@test.com";
        createAndPersistUser(email);
        User user2 = buildUser(email);

        // Act & Assert: Ожидаем PersistenceException или ConstraintViolationException
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            userDao.createUser(user2);
            session.flush(); // Ошибка произойдет здесь
        });

        // Проверяем причину (ConstraintViolationException)
        Throwable cause = ex; // В данном случае само исключение и есть причина (или содержит ее)
        while (cause != null && !(cause instanceof ConstraintViolationException)) {
            cause = cause.getCause();
        }
        assertThat(cause).isInstanceOf(ConstraintViolationException.class);
    }

    // ================== Тесты getUserById ==================

    @Test
    void getUserById_Success_WhenExists_ShouldReturnUser() throws DataAccessException {
        // Arrange
        User persistedUser = createAndPersistUser("get.success@test.com");
        String userId = persistedUser.getId().toString();

        // Act
        Optional<User> foundOpt = userDao.getUserById(userId);

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedUser.getId());
        assertThat(foundOpt.get().getEmail()).isEqualTo("get.success@test.com");
    }

    @Test
    void getUserById_Failure_WhenNotExists_ShouldReturnEmpty() throws DataAccessException {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        // Act
        Optional<User> foundOpt = userDao.getUserById(nonExistentId);
        // Assert
        assertThat(foundOpt).isNotPresent(); // или isEmpty()
    }

    @Test
    void getUserById_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "not-a-uuid";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.getUserById(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(ex.getMessage()).contains("Invalid UUID format");
    }


    // ================== Тесты updateUser ==================

    @Test
    void updateUser_Success_ShouldUpdateFields() throws DataAccessException {
        // Arrange
        User persistedUser = createAndPersistUser("update.success@test.com");
        UUID userId = persistedUser.getId();
        session.clear(); // Очищаем сессию

        // Загружаем сущность, чтобы она стала управляемой в текущей сессии
        User userToUpdate = session.get(User.class, userId);
        assertThat(userToUpdate).isNotNull();
        String newName = "Updated Name";
        String newPhone = "999888777";
        userToUpdate.setName(newName);
        userToUpdate.setPhone(newPhone);
        userToUpdate.setPassword("newpassword"); // Обновляем и другие поля

        // Act
        userDao.updateUser(userToUpdate); // Вызываем merge
        session.flush(); // Применяем изменения
        session.clear(); // Очищаем для проверки

        // Assert
        User updatedUser = session.get(User.class, userId);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getPhone()).isEqualTo(newPhone);
        assertThat(updatedUser.getPassword()).isEqualTo("newpassword"); // Проверяем другие изменения
        assertThat(updatedUser.getEmail()).isEqualTo("update.success@test.com"); // Убедимся, что email не изменился
    }

    @Test
    void updateUser_Failure_SetNullRequiredField_ShouldThrowDataAccessException() throws DataAccessException {
        // Arrange
        User persistedUser = createAndPersistUser("update.fail.null@test.com");
        UUID userId = persistedUser.getId();
        session.clear();
        User userToUpdate = session.get(User.class, userId);
        userToUpdate.setName(null); // Нарушаем not-null constraint для name

        // Act & Assert: Ожидаем PersistenceException или PropertyValueException
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            userDao.updateUser(userToUpdate); // merge
            session.flush(); // Ошибка PropertyValueException при flush
        });

        Throwable rootCause = ex;
        while (rootCause != null && !(rootCause instanceof PropertyValueException)) {
            rootCause = rootCause.getCause();
        }
        assertThat(rootCause).isInstanceOf(PropertyValueException.class)
                .hasMessageContaining("User.name");
    }

    @Test
    void updateUser_Failure_ViolateUniqueConstraint_ShouldThrowDataAccessException() throws DataAccessException {
        // Arrange
        createAndPersistUser("existing.email@test.com");
        User userToUpdate = createAndPersistUser("update.fail.unique@test.com");
        UUID userId = userToUpdate.getId();
        session.clear();
        User loadedUserToUpdate = session.get(User.class, userId);
        loadedUserToUpdate.setEmail("existing.email@test.com"); // Пытаемся изменить @NaturalId

        // Act & Assert: Ожидаем PersistenceException (или HibernateException)
        PersistenceException ex = assertThrows(PersistenceException.class, () -> {
            userDao.updateUser(loadedUserToUpdate); // merge
            session.flush(); // Ошибка изменения @NaturalId или ConstraintViolationException при flush
        });

        // Можно проверить сообщение об ошибке или вложенное исключение
        // В данном случае корень проблемы - HibernateException из-за @NaturalId
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        // Проверка может быть сложной, HibernateException может быть базовым типом
        assertThat(rootCause.getMessage()).contains("immutable natural identifier");
    }

    // ================== Тесты deleteUser ==================

    @Test
    void deleteUser_Success_WhenExists_ShouldRemoveUser() throws DataAccessException {
        // Arrange
        User persistedUser = createAndPersistUser("delete.success@test.com");
        String userId = persistedUser.getId().toString();
        UUID userUUID = persistedUser.getId();

        // Act
        userDao.deleteUser(userId);
        session.flush(); // Применяем remove

        // Assert
        session.clear();
        User deletedUser = session.get(User.class, userUUID);
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteUser_Failure_WhenNotExists_ShouldDoNothing() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();

        // Act & Assert: DAO не должен бросать исключение
        assertDoesNotThrow(() -> {
            userDao.deleteUser(nonExistentId);
            session.flush(); // Убедимся, что flush не вызывает проблем
        });

        // Assert: Проверяем, что ничего не удалилось
        assertThat(session.get(User.class, UUID.fromString(nonExistentId))).isNull();
    }

    @Test
    void deleteUser_Failure_InvalidIdFormat_ShouldThrowDataAccessException() {
        // Arrange
        String invalidId = "not-a-uuid-at-all";
        // Act & Assert
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.deleteUser(invalidId);
        });
        assertThat(ex.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    // ================== Тесты findByEmail ==================

    @Test
    void findByEmail_Success_WhenExists_ShouldReturnUser() throws Exception { // Используем Exception из-за OperationNotSupportedException
        // Arrange
        User persistedUser = createAndPersistUser("find.success@test.com");

        // Act
        Optional<User> foundOpt = userDao.findByEmail("find.success@test.com");

        // Assert
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(persistedUser.getId());
        assertThat(foundOpt.get().getEmail()).isEqualTo("find.success@test.com");
    }

    @Test
    void findByEmail_Failure_WhenNotExists_ShouldReturnEmpty() throws Exception {
        // Arrange
        String nonExistentEmail = "find.fail@test.com";
        // Act
        Optional<User> foundOpt = userDao.findByEmail(nonExistentEmail);
        // Assert
        assertThat(foundOpt).isEmpty();
    }
}