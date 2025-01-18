package data.dao.postgres;


import data.dao.base.BookingDao;
import data.model.record.BookingRecord;
import exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;

import static com.man.constant.Constants.*;
import static com.man.constant.ErrorMessages.BOOKING_CREATION_ERROR;
import static com.man.constant.ErrorMessages.BOOKING_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;

@Slf4j
public class PostgresBookingDao extends AbstractPostgresDao implements BookingDao {

    public PostgresBookingDao(Connection connection) {
        super(connection);
    }

    @Override
    public String createBooking(@NotNull BookingRecord bookingRecord) throws DataAccessException {
        String bookingId = generateId();
        log.info(CREATE_BOOKING_START, bookingRecord.getTripId(), bookingRecord.getUserId());

        try (PreparedStatement statement = connection.prepareStatement(CREATE_BOOKING_SQL)) {
            statement.setObject(1, stringToUUID(bookingId));
            statement.setByte(2, bookingRecord.getSeatCount());
            statement.setString(3, bookingRecord.getStatus());
            statement.setTimestamp(4, new Timestamp(bookingRecord.getBookingDate().getTime()));
            statement.setString(5, bookingRecord.getPassportNumber());
            statement.setTimestamp(6, new Timestamp(bookingRecord.getPassportExpiryDate().getTime()));
            statement.setObject(7, stringToUUID(bookingRecord.getTripId()));
            statement.setObject(8, stringToUUID(bookingRecord.getUserId()));


            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                log.info(CREATE_BOOKING_SUCCESS, bookingId);
                return bookingId;
            } else {
                log.error(ERROR_CREATE_BOOKING, bookingRecord.getTripId(), bookingRecord.getUserId());
                throw new DataAccessException(BOOKING_CREATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ERROR_CREATE_BOOKING, bookingRecord.getTripId(), bookingRecord.getUserId(), e);
            throw new DataAccessException(BOOKING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<BookingRecord> getBookingById(String id) throws DataAccessException {
        log.info(GET_BOOKING_START, id);

        try (PreparedStatement statement = connection.prepareStatement(GET_BOOKING_BY_ID_SQL)) {
            statement.setObject(1, stringToUUID(id));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapToBooking(resultSet));
            } else {
                log.warn(WARN_BOOKING_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(ERROR_GET_BOOKING, id, e);
            throw new DataAccessException(String.format(BOOKING_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateBooking(@NotNull BookingRecord bookingRecord) throws DataAccessException {
        log.info(UPDATE_BOOKING_SUCCESS, bookingRecord.getId());

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_BOOKING_SQL)) {
            statement.setByte(1, bookingRecord.getSeatCount());
            statement.setString(2, bookingRecord.getStatus());
            statement.setTimestamp(3, new Timestamp(bookingRecord.getBookingDate().getTime()));
            statement.setString(4, bookingRecord.getPassportNumber());
            statement.setTimestamp(5, new Timestamp(bookingRecord.getPassportExpiryDate().getTime()));
            statement.setObject(6, stringToUUID(bookingRecord.getTripId()));
            statement.setObject(7, stringToUUID(bookingRecord.getUserId()));
            statement.setObject(8, stringToUUID(bookingRecord.getId()));

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                log.info(UPDATE_BOOKING_SUCCESS, bookingRecord.getId());
            } else {
                log.warn(WARN_BOOKING_NOT_FOUND, bookingRecord.getId());
                throw new DataAccessException(String.format(BOOKING_NOT_FOUND_ERROR, bookingRecord.getId()));
            }
        } catch (SQLException e) {
            log.error(ERROR_UPDATE_BOOKING, bookingRecord.getId(), e);
            throw new DataAccessException(BOOKING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        log.info(DELETE_BOOKING_SUCCESS, id);

        try (PreparedStatement statement = connection.prepareStatement(DELETE_BOOKING_SQL)) {
            statement.setObject(1, stringToUUID(id));

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info(DELETE_BOOKING_SUCCESS, id);
            } else {
                log.warn(WARN_BOOKING_NOT_FOUND, id);
                throw new DataAccessException(String.format(BOOKING_NOT_FOUND_ERROR, id));
            }
        } catch (SQLException e) {
            log.error(ERROR_DELETE_BOOKING, id, e);
            throw new DataAccessException(BOOKING_DELETE_ERROR, e);
        }
    }

    @NotNull
    private BookingRecord mapToBooking(@NotNull ResultSet resultSet) throws SQLException {
        return new BookingRecord(
                resultSet.getString("id"),
                resultSet.getByte("seat_count"),
                resultSet.getString("status"),
                resultSet.getTimestamp("booking_date"),
                resultSet.getString("passport_number"),
                resultSet.getTimestamp("passport_expiry_date"),
                resultSet.getString("trip_id"),
                resultSet.getString("user_id")
        );
    }
}

