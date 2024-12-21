package providers;


import lombok.extern.slf4j.Slf4j;
import model.database.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import utils.PostgresConnectionUtil;

import java.sql.*;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class JdbcDataProviderTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private JdbcDataProvider dataProvider;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        dataProvider = new JdbcDataProvider(mockConnection);
    }

    @Test
    public void testCreateUser() throws SQLException {
        User user = new User(1, "John Doe", "john.doe@example.com", "password123", "M", "123456789", new java.util.Date(), "123 Main St", "none");

        // Выполнение метода
        dataProvider.createUser(user);

        // Проверка, что подготовленное заявление было вызвано
        verify(mockStatement, times(1)).setString(1, user.getName());
        verify(mockStatement, times(1)).setString(2, user.getEmail());
        verify(mockStatement, times(1)).setString(3, user.getPassword());
        verify(mockStatement, times(1)).setString(4, user.getGender());
        verify(mockStatement, times(1)).setString(5, user.getPhone());
        verify(mockStatement, times(1)).setDate(6, new java.sql.Date(user.getBirthDate().getTime()));
        verify(mockStatement, times(1)).setString(7, user.getAddress());
        verify(mockStatement, times(1)).setString(8, user.getPreferences());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetUserById() throws SQLException {
        int userId = 1;
        User expectedUser = new User(userId, "John Doe", "john.doe@example.com", "password123", "M", "123456789", new java.util.Date(), "123 Main St", "none");

        // Настройка мокового ResultSet
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(userId);
        when(mockResultSet.getString("name")).thenReturn(expectedUser.getName());
        when(mockResultSet.getString("email")).thenReturn(expectedUser.getEmail());
        when(mockResultSet.getString("password")).thenReturn(expectedUser.getPassword());
        when(mockResultSet.getString("gender")).thenReturn(expectedUser.getGender());
        when(mockResultSet.getString("phone")).thenReturn(expectedUser.getPhone());
        when(mockResultSet.getDate("birthDate")).thenReturn(new java.sql.Date(expectedUser.getBirthDate().getTime()));
        when(mockResultSet.getString("address")).thenReturn(expectedUser.getAddress());
        when(mockResultSet.getString("preferences")).thenReturn(expectedUser.getPreferences());

        // Выполнение метода
        User result = dataProvider.getUserById(userId);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(expectedUser.getName(), result.getName());
        assertEquals(expectedUser.getEmail(), result.getEmail());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User(1, "John Doe", "john.doe@example.com", "newPassword123", "M", "123456789", new java.util.Date(), "123 Main St", "none");

        // Выполнение метода
        dataProvider.updateUser(user);

        // Проверка, что обновление прошло
        verify(mockStatement, times(1)).setString(1, user.getName());
        verify(mockStatement, times(1)).setString(2, user.getEmail());
        verify(mockStatement, times(1)).setString(3, user.getPassword());
        verify(mockStatement, times(1)).setString(4, user.getGender());
        verify(mockStatement, times(1)).setString(5, user.getPhone());
        verify(mockStatement, times(1)).setDate(6, new java.sql.Date(user.getBirthDate().getTime()));
        verify(mockStatement, times(1)).setString(7, user.getAddress());
        verify(mockStatement, times(1)).setString(8, user.getPreferences());
        verify(mockStatement, times(1)).setInt(9, user.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteUser() throws SQLException {
        int userId = 1;

        // Выполнение метода
        dataProvider.deleteUser(userId);

        // Проверка, что delete был выполнен
        verify(mockStatement, times(1)).setInt(1, userId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateTrip() throws SQLException {
        Trip trip = new Trip(
                1,
                new java.util.Date(),
                (byte) 4,
                new java.util.Date(),
                "Scheduled",
                true
        );
        int userId = 1;
        int routeId = 2;

        dataProvider.createTrip(trip, userId, routeId);

        verify(mockStatement, times(1)).setTimestamp(eq(1), any(Timestamp.class));
        verify(mockStatement, times(1)).setByte(2, trip.getMaxPassengers());
        verify(mockStatement, times(1)).setTimestamp(eq(3), any(Timestamp.class));
        verify(mockStatement, times(1)).setString(4, trip.getStatus());
        verify(mockStatement, times(1)).setBoolean(5, trip.isEditable());
        verify(mockStatement, times(1)).setInt(6, userId);
        verify(mockStatement, times(1)).setInt(7, routeId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetTripById() throws SQLException {
        int tripId = 1;
        Trip expectedTrip = new Trip(
                tripId,
                new java.util.Date(),
                (byte) 4,
                new java.util.Date(),
                "Scheduled",
                true
        );

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(tripId);
        when(mockResultSet.getTimestamp("departureTime")).thenReturn(new Timestamp(expectedTrip.getDepartureTime().getTime()));
        when(mockResultSet.getByte("maxPassengers")).thenReturn(expectedTrip.getMaxPassengers());
        when(mockResultSet.getTimestamp("creationDate")).thenReturn(new Timestamp(expectedTrip.getCreationDate().getTime()));
        when(mockResultSet.getString("status")).thenReturn(expectedTrip.getStatus());
        when(mockResultSet.getBoolean("editable")).thenReturn(expectedTrip.isEditable());

        Trip result = dataProvider.getTripById(tripId);

        assertNotNull(result);
        assertEquals(expectedTrip.getId(), result.getId());
        assertEquals(expectedTrip.getStatus(), result.getStatus());
        assertEquals(expectedTrip.getMaxPassengers(), result.getMaxPassengers());
    }

    @Test
    public void testUpdateTrip() throws SQLException {
        Trip trip = new Trip(
                1,
                new java.util.Date(),
                (byte) 4,
                new java.util.Date(),
                "Completed",
                false
        );
        int newRouteId = 3;

        dataProvider.updateTrip(trip, newRouteId);

        verify(mockStatement, times(1)).setTimestamp(eq(1), any(Timestamp.class));
        verify(mockStatement, times(1)).setByte(2, trip.getMaxPassengers());
        verify(mockStatement, times(1)).setTimestamp(eq(3), any(Timestamp.class));
        verify(mockStatement, times(1)).setString(4, trip.getStatus());
        verify(mockStatement, times(1)).setBoolean(5, trip.isEditable());
        verify(mockStatement, times(1)).setInt(6, newRouteId);
        verify(mockStatement, times(1)).setInt(7, trip.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteTrip() throws SQLException {
        int tripId = 1;

        dataProvider.deleteTrip(tripId);

        verify(mockStatement, times(1)).setInt(1, tripId);
        verify(mockStatement, times(1)).executeUpdate();
    }


    @Test
    public void testCreateRoute() throws SQLException {
        Route route = new Route(1, "Start Point", "End Point", new java.util.Date(), (short) 120);

        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        int routeId = dataProvider.createRoute(route);

        assertEquals(1, routeId);
        verify(mockStatement, times(1)).setString(1, route.getStartPoint());
        verify(mockStatement, times(1)).setString(2, route.getEndPoint());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setShort(4, route.getEstimatedDuration());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetRouteById() throws SQLException {
        int routeId = 1;
        Route expectedRoute = new Route(
                routeId,
                "Start Point",
                "End Point",
                new java.util.Date(),
                (short) 120
        );

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(routeId);
        when(mockResultSet.getString("startPoint")).thenReturn(expectedRoute.getStartPoint());
        when(mockResultSet.getString("endPoint")).thenReturn(expectedRoute.getEndPoint());
        when(mockResultSet.getDate("date")).thenReturn(new java.sql.Date(expectedRoute.getDate().getTime()));
        when(mockResultSet.getShort("estimatedDuration")).thenReturn(expectedRoute.getEstimatedDuration());

        Route result = dataProvider.getRouteById(routeId);

        assertNotNull(result);
        assertEquals(expectedRoute.getId(), result.getId());
        assertEquals(expectedRoute.getStartPoint(), result.getStartPoint());
        assertEquals(expectedRoute.getEndPoint(), result.getEndPoint());
        assertEquals(expectedRoute.getEstimatedDuration(), result.getEstimatedDuration());
    }

    @Test
    public void testUpdateRoute() throws SQLException {
        Route route = new Route(1, "New Start Point", "New End Point", new java.util.Date(), (short) 100);

        dataProvider.updateRoute(route);

        verify(mockStatement, times(1)).setString(1, route.getStartPoint());
        verify(mockStatement, times(1)).setString(2, route.getEndPoint());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setShort(4, route.getEstimatedDuration());
        verify(mockStatement, times(1)).setInt(5, route.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteRoute() throws SQLException {
        int routeId = 1;

        dataProvider.deleteRoute(routeId);

        verify(mockStatement, times(1)).setInt(1, routeId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateBooking() throws SQLException {
        Booking booking = new Booking(
                1,
                (byte) 2,
                "Confirmed",
                new java.util.Date(),
                "123456789",
                new java.util.Date()
        );
        int tripId = 1;
        int userId = 2;

        dataProvider.createBooking(booking, tripId, userId);

        verify(mockStatement, times(1)).setByte(1, booking.getSeatCount());
        verify(mockStatement, times(1)).setString(2, booking.getStatus());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setString(4, booking.getPassportNumber());
        verify(mockStatement, times(1)).setDate(eq(5), any(Date.class));
        verify(mockStatement, times(1)).setInt(6, tripId);
        verify(mockStatement, times(1)).setInt(7, userId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetBookingById() throws SQLException {
        int bookingId = 1;
        Booking expectedBooking = new Booking(
                bookingId,
                (byte) 2,
                "Confirmed",
                new java.util.Date(),
                "123456789",
                new java.util.Date()
        );

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(bookingId);
        when(mockResultSet.getByte("seatCount")).thenReturn(expectedBooking.getSeatCount());
        when(mockResultSet.getString("status")).thenReturn(expectedBooking.getStatus());
        when(mockResultSet.getDate("bookingDate")).thenReturn(new java.sql.Date(expectedBooking.getBookingDate().getTime()));
        when(mockResultSet.getString("passportNumber")).thenReturn(expectedBooking.getPassportNumber());
        when(mockResultSet.getDate("passportExpiryDate")).thenReturn(new java.sql.Date(expectedBooking.getPassportExpiryDate().getTime()));

        Booking result = dataProvider.getBookingById(bookingId);

        assertNotNull(result);
        assertEquals(expectedBooking.getId(), result.getId());
        assertEquals(expectedBooking.getSeatCount(), result.getSeatCount());
        assertEquals(expectedBooking.getStatus(), result.getStatus());
        assertEquals(expectedBooking.getPassportNumber(), result.getPassportNumber());
    }

    @Test
    public void testUpdateBooking() throws SQLException {
        Booking booking = new Booking(
                1,
                (byte) 3,
                "Cancelled",
                new java.util.Date(),
                "987654321",
                new java.util.Date()
        );

        dataProvider.updateBooking(booking);

        verify(mockStatement, times(1)).setByte(1, booking.getSeatCount());
        verify(mockStatement, times(1)).setString(2, booking.getStatus());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setString(4, booking.getPassportNumber());
        verify(mockStatement, times(1)).setDate(eq(5), any(Date.class));
        verify(mockStatement, times(1)).setInt(6, booking.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteBooking() throws SQLException {
        int bookingId = 1;

        dataProvider.deleteBooking(bookingId);

        verify(mockStatement, times(1)).setInt(1, bookingId);
        verify(mockStatement, times(1)).executeUpdate();
    }


    @Test
    public void testCreateRating() throws SQLException {
        Rating rating = new Rating(1,5, "Excellent Trip", new java.util.Date());
        int tripId = 1;

        dataProvider.createRating(rating, tripId);

        verify(mockStatement, times(1)).setInt(1, rating.getRating());
        verify(mockStatement, times(1)).setString(2, rating.getComment());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setInt(4, tripId);
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetRatingById() throws SQLException {
        int ratingId = 1;
        Rating expectedRating = new Rating(ratingId, 5, "Excellent Trip", new java.util.Date());

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(ratingId);
        when(mockResultSet.getInt("rating")).thenReturn(expectedRating.getRating());
        when(mockResultSet.getString("comment")).thenReturn(expectedRating.getComment());
        when(mockResultSet.getDate("date")).thenReturn(new java.sql.Date(expectedRating.getDate().getTime()));

        Rating result = dataProvider.getRatingById(ratingId);

        assertNotNull(result);
        assertEquals(expectedRating.getId(), result.getId());
        assertEquals(expectedRating.getRating(), result.getRating());
        assertEquals(expectedRating.getComment(), result.getComment());
    }

    @Test
    public void testUpdateRating() throws SQLException {
        Rating rating = new Rating(1, 4, "Good Trip", new java.util.Date());

        dataProvider.updateRating(rating);

        verify(mockStatement, times(1)).setInt(1, rating.getRating());
        verify(mockStatement, times(1)).setString(2, rating.getComment());
        verify(mockStatement, times(1)).setDate(eq(3), any(Date.class));
        verify(mockStatement, times(1)).setInt(4, rating.getId());
        verify(mockStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteRating() throws SQLException {
        int ratingId = 1;

        dataProvider.deleteRating(ratingId);

        verify(mockStatement, times(1)).setInt(1, ratingId);
        verify(mockStatement, times(1)).executeUpdate();
    }







    @AfterEach
    public void tearDown() {

    }






    @Test
    public void first() throws SQLException {
        JdbcDataProvider jdbcDataProvider = new JdbcDataProvider(
                PostgresConnectionUtil.getConnection()
        );
        User user = new User(
                1,
                "test",
                "test@test.ru",
                "123456",
                "male",
                "89123456789",
                java.sql.Date.valueOf("2000-01-01"),
                "test address",
                "test preferences"
        );
        jdbcDataProvider.createUser(user);
        log.info("User created successfully");
        PostgresConnectionUtil.closeConnection(jdbcDataProvider.connection());
    }
}
