package dao.postgres;


import com.carpooling.dao.postgres.PostgresRatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostgresRatingDaoTest {

    private Connection mockConnection;
    private PostgresRatingDao ratingDao;

    @BeforeEach
    void setUp() {
        mockConnection = mock(Connection.class);
        ratingDao = new PostgresRatingDao(mockConnection);
    }

    @Test
    void createRating_Success() throws SQLException {

        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        String tripId = UUID.randomUUID().toString();

        RatingRecord ratingRecord = new RatingRecord(null, 5, "Excellent service", new Date(), tripId);

        String result = ratingDao.createRating(ratingRecord);

        assertNotNull(result);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void createRating_Failure() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        String tripId = UUID.randomUUID().toString();

        RatingRecord ratingRecord = new RatingRecord(null, 5, "Excellent service", new Date(), tripId);

        assertThrows(DataAccessException.class, () -> ratingDao.createRating(ratingRecord));
    }

    @Test
    void getRatingById_Success() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        String Id = UUID.randomUUID().toString();

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("id")).thenReturn(Id);
        when(mockResultSet.getInt("rating")).thenReturn(5);
        when(mockResultSet.getString("comment")).thenReturn("Excellent service");
        when(mockResultSet.getTimestamp("date")).thenReturn(new Timestamp(new Date().getTime()));

        Optional<RatingRecord> result = ratingDao.getRatingById(Id);

        assertTrue(result.isPresent());
        assertEquals(Id, result.get().getId());
        verify(mockStatement).executeQuery();
    }

    @Test
    void getRatingById_NotFound() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        String id = UUID.randomUUID().toString();

        Optional<RatingRecord> result = ratingDao.getRatingById(id);

        assertFalse(result.isPresent());
        verify(mockStatement).executeQuery();
    }

    @Test
    void updateRating_Success() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        RatingRecord ratingRecord = new RatingRecord(UUID.randomUUID().toString(), 4, "Good service", new Date(), UUID.randomUUID().toString());

        assertDoesNotThrow(() -> ratingDao.updateRating(ratingRecord));

        verify(mockStatement).executeUpdate();
    }

    @Test
    void updateRating_Failure() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);

        String id = UUID.randomUUID().toString();
        RatingRecord ratingRecord = new RatingRecord(id, 4, "Good service", new Date(), id);

        assertThrows(DataAccessException.class, () -> ratingDao.updateRating(ratingRecord));
    }

    @Test
    void deleteRating_Success() throws SQLException {

        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> ratingDao.deleteRating(UUID.randomUUID().toString()));

        verify(mockStatement).executeUpdate();
    }

    @Test
    void deleteRating_Failure() throws SQLException {
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);

        assertThrows(DataAccessException.class, () -> ratingDao.deleteRating(UUID.randomUUID().toString()));
    }
}



