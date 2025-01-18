package dao.postgres;

import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.record.UserRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostgresUserDaoTest {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private PostgresUserDao userDao;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        userDao = new PostgresUserDao(connection);
    }



    @Test
    void createUser_Success() throws SQLException {
        String id = UUID.randomUUID().toString();
        UserRecord userRecord = createUserRecord(id);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        String userId = userDao.createUser(userRecord);

        assertNotNull(userId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void createUser_Failure_SQLException() throws SQLException {
        UserRecord userRecord = createUserRecord(UUID.randomUUID().toString());

        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(DataAccessException.class, () -> userDao.createUser(userRecord));
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void getUserById_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        String id = UUID.randomUUID().toString();
        when(resultSet.getString("id")).thenReturn(id);
        when(resultSet.getString("name")).thenReturn("Test Name");

        Optional<UserRecord> user = userDao.getUserById(id);

        assertTrue(user.isPresent());
        assertEquals(id, user.get().getId());
        assertEquals("Test Name", user.get().getName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void getUserById_NotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        String id = UUID.randomUUID().toString();

        Optional<UserRecord> user = userDao.getUserById(id);

        assertTrue(user.isEmpty());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void updateUser_Success() throws SQLException {
        String id = UUID.randomUUID().toString();
        UserRecord userRecord = new UserRecord(id, "Updated Name", "updated@example.com", "password456",
                "Male", "987654321", new Date(), "456 Updated St", "Updated Preferences");

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userDao.updateUser(userRecord));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void updateUser_NotFound() throws SQLException {
        String id = UUID.randomUUID().toString();
        UserRecord userRecord = createUserRecord(id);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> userDao.updateUser(userRecord));
        assertEquals("Пользователь с ID " + id + " не найден.", exception.getMessage());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void deleteUser_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userDao.deleteUser(UUID.randomUUID().toString()));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void deleteUser_NotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        String id = UUID.randomUUID().toString();

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> userDao.deleteUser(id));
        assertEquals("Пользователь с ID " + id + " не найден.", exception.getMessage());
        verify(preparedStatement, times(1)).executeUpdate();
    }


    private UserRecord createUserRecord(String id) {
        return new UserRecord(
                id,
                "Test User",
                "test@example.com",
                "password",
                "Male",
                "123-456-7890",
                Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "Test Address",
                "Test Preferences"
        );
    }

}