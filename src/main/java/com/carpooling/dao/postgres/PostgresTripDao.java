package com.carpooling.dao.postgres;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.record.TripRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.TRIP_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.TRIP_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;
import static com.carpooling.constants.Constants.*;

@Slf4j
public class PostgresTripDao extends AbstractPostgresDao implements TripDao {

    public PostgresTripDao(Connection connection) {
        super(connection);
    }

    @Override
    public String createTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        String tripId = generateId();
        log.info(CREATE_TRIP_START, tripRecord.getUserId(), tripRecord.getRouteId());

        try (PreparedStatement statement = connection.prepareStatement(CREATE_TRIP_SQL)) {
            statement.setObject(1, stringToUUID(tripId));
            statement.setTimestamp(2, new Timestamp(tripRecord.getDepartureTime().getTime()));
            statement.setByte(3, tripRecord.getMaxPassengers());
            statement.setTimestamp(4, new Timestamp(tripRecord.getCreationDate().getTime()));
            statement.setString(5, tripRecord.getStatus());
            statement.setBoolean(6, tripRecord.isEditable());
            statement.setObject(7, stringToUUID(tripRecord.getUserId()));
            statement.setObject(8, stringToUUID(tripRecord.getRouteId()));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                log.info(CREATE_TRIP_SUCCESS, tripId);
                return tripId;
            } else {
                log.error(ERROR_CREATE_TRIP, tripRecord.getUserId(), tripRecord.getRouteId());
                throw new DataAccessException(TRIP_CREATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ERROR_CREATE_TRIP, tripRecord.getUserId(), tripRecord.getRouteId(), e);
            throw new DataAccessException(TRIP_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<TripRecord> getTripById(String id) throws DataAccessException {
        log.info(GET_TRIP_START, id);

        try (PreparedStatement statement = connection.prepareStatement(GET_TRIP_BY_ID_SQL)) {
            statement.setObject(1, stringToUUID(id));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapToTrip(resultSet));
            } else {
                log.warn(WARN_TRIP_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(ERROR_GET_TRIP, id, e);
            throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        log.info(UPDATE_TRIP_SUCCESS, tripRecord.getId());

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TRIP_SQL)) {
            statement.setTimestamp(1, new Timestamp(tripRecord.getDepartureTime().getTime()));
            statement.setByte(2, tripRecord.getMaxPassengers());
            statement.setTimestamp(3, new Timestamp(tripRecord.getCreationDate().getTime()));
            statement.setString(4, tripRecord.getStatus());
            statement.setBoolean(5, tripRecord.isEditable());
            statement.setObject(6, stringToUUID(tripRecord.getUserId()));
            statement.setObject(7, stringToUUID(tripRecord.getRouteId()));
            statement.setObject(8, stringToUUID(tripRecord.getId()));


            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                log.info(UPDATE_TRIP_SUCCESS, tripRecord.getId());
            } else {
                log.warn(WARN_TRIP_NOT_FOUND, tripRecord.getId());
                throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, tripRecord.getId()));
            }
        } catch (SQLException e) {
            log.error(ERROR_UPDATE_TRIP, tripRecord.getId(), e);
            throw new DataAccessException(TRIP_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        log.info(DELETE_TRIP_SUCCESS, id);

        try (PreparedStatement statement = connection.prepareStatement(DELETE_TRIP_SQL)) {
            statement.setObject(1, stringToUUID(id));

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info(DELETE_TRIP_SUCCESS, id);
            } else {
                log.warn(WARN_TRIP_NOT_FOUND, id);
                throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, id));
            }
        } catch (SQLException e) {
            log.error(ERROR_DELETE_TRIP, id, e);
            throw new DataAccessException(TRIP_DELETE_ERROR, e);
        }
    }

    @NotNull
    private TripRecord mapToTrip(@NotNull ResultSet resultSet) throws SQLException {
        return new TripRecord(
                resultSet.getString("id"),
                resultSet.getTimestamp("departure_time"),
                resultSet.getByte("max_passengers"),
                resultSet.getTimestamp("creation_date"),
                resultSet.getString("status"),
                resultSet.getBoolean("editable"),
                resultSet.getString("user_id"),
                resultSet.getString("route_id")
        );
    }
}
