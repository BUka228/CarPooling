package data.dao.postgres;


import data.dao.base.RouteDao;
import data.model.database.Route;
import data.model.record.RouteRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostgresRouteDaoTest {
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private RouteDao routeDao;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        routeDao = new PostgresRouteDao(mockConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        mockConnection.close();
    }

    @Test
    void createRoute_success() throws SQLException {
        RouteRecord routeRecord = new RouteRecord(null, "Start", "End", new Date(), (short) 120);

        when(mockStatement.executeUpdate()).thenReturn(1);

        String routeId = routeDao.createRoute(routeRecord);

        assertNotNull(routeId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    void createRoute_failure() throws SQLException {
        RouteRecord routeRecord = new RouteRecord(null, "Start", "End", new Date(), (short) 120);

        when(mockStatement.executeUpdate()).thenReturn(0);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> routeDao.createRoute(routeRecord));
        assertEquals("Ошибка при создании маршрута.", exception.getMessage());
    }

    @Test
    void getRouteById_success() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        String routeId = UUID.randomUUID().toString();
        when(mockResultSet.getString("id")).thenReturn(routeId);
        when(mockResultSet.getString("start_point")).thenReturn("Start");
        when(mockResultSet.getString("end_point")).thenReturn("End");
        when(mockResultSet.getTimestamp("date")).thenReturn(new Timestamp(new Date().getTime()));
        when(mockResultSet.getShort("estimated_duration")).thenReturn((short) 120);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        Optional<RouteRecord> result = routeDao.getRouteById(routeId);

        assertTrue(result.isPresent());
        assertEquals(routeId, result.get().getId());
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void getRouteById_failure() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        Optional<RouteRecord> result = routeDao.getRouteById(UUID.randomUUID().toString());

        assertTrue(result.isEmpty());
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void updateRoute_success() throws SQLException {
        RouteRecord routeRecord = new RouteRecord(UUID.randomUUID().toString(), "UpdatedStart", "UpdatedEnd", new Date(), (short) 150);

        when(mockStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> routeDao.updateRoute(routeRecord));
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    void updateRoute_failure() throws SQLException {
        String routeId = UUID.randomUUID().toString();
        RouteRecord routeRecord = new RouteRecord(routeId, "UpdatedStart", "UpdatedEnd", new Date(), (short) 150);

        when(mockStatement.executeUpdate()).thenReturn(0);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> routeDao.updateRoute(routeRecord));
        assertEquals("Маршрут с ID " + routeId + " не найден.", exception.getMessage());
    }

    @Test
    void deleteRoute_success() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> routeDao.deleteRoute(UUID.randomUUID().toString()));
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    void deleteRoute_failure() throws SQLException {
        when(mockStatement.executeUpdate()).thenReturn(0);
        String routeId = UUID.randomUUID().toString();


        DataAccessException exception = assertThrows(DataAccessException.class, () -> routeDao.deleteRoute(routeId));
        assertEquals("Маршрут с ID " + routeId + " не найден.", exception.getMessage());
    }
}