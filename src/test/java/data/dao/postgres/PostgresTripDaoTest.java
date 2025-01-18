package data.dao.postgres;

import data.model.database.Trip;
import data.model.record.TripRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostgresTripDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private PostgresTripDao tripDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tripDao = new PostgresTripDao(connection);
    }

    @Test
    void testCreateTrip_Success() throws Exception {
        // Arrange
        String routeId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        TripRecord tripRecord = new TripRecord();
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("SCHEDULED");
        tripRecord.setEditable(true);
        tripRecord.setUserId(userId);
        tripRecord.setRouteId(routeId);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        String tripId = tripDao.createTrip(tripRecord);

        // Assert
        assertNotNull(tripId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testCreateTrip_Failure() throws Exception {
        String routeId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        TripRecord tripRecord = new TripRecord();
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("SCHEDULED");
        tripRecord.setEditable(true);
        tripRecord.setUserId(userId);
        tripRecord.setRouteId(routeId);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.createTrip(tripRecord));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetTripById_Success() throws Exception {
        // Arrange
        String tripId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn(tripId);
        when(resultSet.getTimestamp("departure_time")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getByte("max_passengers")).thenReturn((byte) 4);
        when(resultSet.getTimestamp("creation_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("status")).thenReturn("SCHEDULED");
        when(resultSet.getBoolean("editable")).thenReturn(true);
        when(resultSet.getString("user_id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("route_id")).thenReturn(UUID.randomUUID().toString());


        // Act
        Optional<TripRecord> trip = tripDao.getTripById(tripId);

        // Assert
        assertTrue(trip.isPresent());
        assertEquals(tripId, trip.get().getId());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetTripById_NotFound() throws Exception {
        // Arrange
        String tripId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<TripRecord> trip = tripDao.getTripById(tripId);

        // Assert
        assertFalse(trip.isPresent());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateTrip_Success() throws Exception {
        // Arrange
        TripRecord tripRecord = new TripRecord();
        tripRecord.setId(UUID.randomUUID().toString());
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("SCHEDULED");
        tripRecord.setEditable(true);
        tripRecord.setUserId(UUID.randomUUID().toString());
        tripRecord.setRouteId(UUID.randomUUID().toString());

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        tripDao.updateTrip(tripRecord);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateTrip_NotFound() throws Exception {
        // Arrange
        TripRecord tripRecord = new TripRecord();
        tripRecord.setId(UUID.randomUUID().toString());
        tripRecord.setDepartureTime(new Date());
        tripRecord.setMaxPassengers((byte) 4);
        tripRecord.setCreationDate(new Date());
        tripRecord.setStatus("SCHEDULED");
        tripRecord.setEditable(true);
        tripRecord.setUserId(UUID.randomUUID().toString());
        tripRecord.setRouteId(UUID.randomUUID().toString());

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.updateTrip(tripRecord));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteTrip_Success() throws Exception {
        // Arrange
        String tripId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        tripDao.deleteTrip(tripId);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteTrip_NotFound() throws Exception {
        // Arrange
        String tripId = UUID.randomUUID().toString();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip(tripId));
        verify(preparedStatement, times(1)).executeUpdate();
    }
}