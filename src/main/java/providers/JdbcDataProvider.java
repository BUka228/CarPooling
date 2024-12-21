package providers;

import lombok.extern.slf4j.Slf4j;
import model.database.*;

import java.sql.*;


@Slf4j
public record JdbcDataProvider(Connection connection) {

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, email, password, gender, phone, birthDate, address, preferences) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getPhone());
            ps.setDate(6, new java.sql.Date(user.getBirthDate().getTime()));
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getPreferences());
            ps.executeUpdate();
        }
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getDate("birthDate"),
                        rs.getString("address"),
                        rs.getString("preferences")
                );
            }
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET name = ?, email = ?, password = ?, gender = ?, phone = ?, birthDate = ?, address = ?, preferences = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getPhone());
            ps.setDate(6, new java.sql.Date(user.getBirthDate().getTime()));
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getPreferences());
            ps.setInt(9, user.getId());
            ps.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // CRUD Operations for Trip
    public void createTrip(Trip trip, int userId, int routeId) throws SQLException {
        String sql = "INSERT INTO Trip (departureTime, maxPassengers, creationDate, status, editable, userId, routeId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(trip.getDepartureTime().getTime()));
            ps.setByte(2, trip.getMaxPassengers());
            ps.setTimestamp(3, new Timestamp(trip.getCreationDate().getTime()));
            ps.setString(4, trip.getStatus());
            ps.setBoolean(5, trip.isEditable());
            ps.setInt(6, userId);
            ps.setInt(7, routeId);
            ps.executeUpdate();
        }
    }

    public Trip getTripById(int id) throws SQLException {
        String sql = "SELECT * FROM Trip WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Trip(
                        rs.getInt("id"),
                        rs.getTimestamp("departureTime"),
                        rs.getByte("maxPassengers"),
                        rs.getTimestamp("creationDate"),
                        rs.getString("status"),
                        rs.getBoolean("editable")
                );
            }
        }
        return null;
    }

    public void updateTrip(Trip trip, int routeId) throws SQLException {
        String sql = "UPDATE Trip SET departureTime = ?, maxPassengers = ?, creationDate = ?, status = ?, editable = ?, routeId = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(trip.getDepartureTime().getTime()));
            ps.setByte(2, trip.getMaxPassengers());
            ps.setTimestamp(3, new Timestamp(trip.getCreationDate().getTime()));
            ps.setString(4, trip.getStatus());
            ps.setBoolean(5, trip.isEditable());
            ps.setInt(6, routeId); // Новый маршрут
            ps.setInt(7, trip.getId());
            ps.executeUpdate();
        }
    }

    public void deleteTrip(int id) throws SQLException {
        String sql = "DELETE FROM Trip WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int createRoute(Route route) throws SQLException {
        String sql = "INSERT INTO Route (startPoint, endPoint, date, estimatedDuration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            log.info(route.getStartPoint());
            ps.setString(1, route.getStartPoint());
            log.info(route.getStartPoint());
            ps.setString(2, route.getEndPoint());
            ps.setDate(3, new java.sql.Date(route.getDate().getTime()));
            ps.setShort(4, route.getEstimatedDuration());
            ps.executeUpdate();

            // Получение сгенерированного id
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create Route, no ID obtained.");
            }
        }
    }

    public Route getRouteById(int id) throws SQLException {
        String sql = "SELECT * FROM Route WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Route(
                        rs.getInt("id"),
                        rs.getString("startPoint"),
                        rs.getString("endPoint"),
                        rs.getDate("date"),
                        rs.getShort("estimatedDuration")
                );
            }
        }
        return null;
    }

    public void updateRoute(Route route) throws SQLException {
        String sql = "UPDATE Route SET startPoint = ?, endPoint = ?, date = ?, estimatedDuration = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, route.getStartPoint());
            ps.setString(2, route.getEndPoint());
            ps.setDate(3, new java.sql.Date(route.getDate().getTime()));
            ps.setShort(4, route.getEstimatedDuration());
            ps.setInt(5, route.getId());
            ps.executeUpdate();
        }
    }

    public void deleteRoute(int id) throws SQLException {
        String sql = "DELETE FROM Route WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    public void createRating(Rating rating, int tripId) throws SQLException {
        String sql = "INSERT INTO Rating (rating, comment, date, tripId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating.getRating());
            ps.setString(2, rating.getComment());
            ps.setDate(3, new java.sql.Date(rating.getDate().getTime()));
            ps.setInt(4, tripId);
            ps.executeUpdate();
        }
    }

    public Rating getRatingById(int id) throws SQLException {
        String sql = "SELECT * FROM Rating WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Rating(
                        rs.getInt("id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getDate("date")
                );
            }
        }
        return null;
    }

    public void updateRating(Rating rating) throws SQLException {
        String sql = "UPDATE Rating SET rating = ?, comment = ?, date = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating.getRating());
            ps.setString(2, rating.getComment());
            ps.setDate(3, new java.sql.Date(rating.getDate().getTime()));
            ps.setInt(4, rating.getId());
            ps.executeUpdate();
        }
    }

    public void deleteRating(int id) throws SQLException {
        String sql = "DELETE FROM Rating WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    public void createBooking(Booking booking, int tripId, int userId) throws SQLException {
        String sql = "INSERT INTO Booking (seatCount, status, bookingDate, passportNumber, passportExpiryDate, tripId, userId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setByte(1, booking.getSeatCount());
            ps.setString(2, booking.getStatus());
            ps.setDate(3, new java.sql.Date(booking.getBookingDate().getTime()));
            ps.setString(4, booking.getPassportNumber());
            ps.setDate(5, new java.sql.Date(booking.getPassportExpiryDate().getTime()));
            ps.setInt(6, tripId);
            ps.setInt(7, userId);
            ps.executeUpdate();
        }
    }

    public Booking getBookingById(int id) throws SQLException {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("id"),
                        rs.getByte("seatCount"),
                        rs.getString("status"),
                        rs.getDate("bookingDate"),
                        rs.getString("passportNumber"),
                        rs.getDate("passportExpiryDate")
                );
            }
        }
        return null;
    }

    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE Booking SET seatCount = ?, status = ?, bookingDate = ?, passportNumber = ?, passportExpiryDate = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setByte(1, booking.getSeatCount());
            ps.setString(2, booking.getStatus());
            ps.setDate(3, new java.sql.Date(booking.getBookingDate().getTime()));
            ps.setString(4, booking.getPassportNumber());
            ps.setDate(5, new java.sql.Date(booking.getPassportExpiryDate().getTime()));
            ps.setInt(6, booking.getId());
            ps.executeUpdate();
        }
    }

    public void deleteBooking(int id) throws SQLException {
        String sql = "DELETE FROM Booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
