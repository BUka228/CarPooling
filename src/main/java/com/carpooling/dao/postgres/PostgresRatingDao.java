package com.carpooling.dao.postgres;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.record.RatingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.RATING_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.RATING_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;
import static com.carpooling.constants.Constants.*;

@Slf4j
public class PostgresRatingDao extends AbstractPostgresDao implements RatingDao {

    public PostgresRatingDao(Connection connection) {
        super(connection);
    }

    @Override
    public String createRating(@NotNull RatingRecord ratingRecord) throws DataAccessException {
        String ratingId = generateId();

        try (PreparedStatement statement = connection.prepareStatement(CREATE_RATING_SQL)) {
            statement.setObject(1, stringToUUID(ratingId));
            statement.setInt(2, ratingRecord.getRating());
            statement.setString(3, ratingRecord.getComment());
            statement.setTimestamp(4, new Timestamp(ratingRecord.getDate().getTime()));
            statement.setObject(5, stringToUUID(ratingRecord.getTripId()));


            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                log.info(CREATE_RATING_SUCCESS, ratingId);
                return ratingId;
            } else {
                log.error(ERROR_CREATE_RATING, ratingRecord.getTripId());
                throw new DataAccessException(RATING_CREATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ERROR_CREATE_RATING, ratingRecord.getTripId(), e);
            throw new DataAccessException(RATING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RatingRecord> getRatingById(String id) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(GET_RATING_BY_ID_SQL)) {
            statement.setObject(1, stringToUUID(id));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.info(GET_RATING_START, id);
                return Optional.of(mapToRating(resultSet));
            } else {
                log.warn(WARN_RATING_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(ERROR_GET_RATING, id, e);
            throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRating(@NotNull RatingRecord ratingRecord) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_RATING_SQL)) {
            statement.setInt(1, ratingRecord.getRating());
            statement.setString(2, ratingRecord.getComment());
            statement.setTimestamp(3, new Timestamp(ratingRecord.getDate().getTime()));
            statement.setObject(4, stringToUUID(ratingRecord.getTripId()));
            statement.setObject(5, stringToUUID(ratingRecord.getId()));

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                log.info(UPDATE_RATING_SUCCESS, ratingRecord.getId());
            } else {
                log.warn(WARN_RATING_NOT_FOUND, ratingRecord.getId());
                throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, ratingRecord.getId()));
            }
        } catch (SQLException e) {
            log.error(ERROR_UPDATE_RATING, ratingRecord.getId(), e);
            throw new DataAccessException(RATING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_RATING_SQL)) {
            statement.setObject(1, stringToUUID(id));

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info(DELETE_RATING_SUCCESS, id);
            } else {
                log.warn(WARN_RATING_NOT_FOUND, id);
                throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, id));
            }
        } catch (SQLException e) {
            log.error(ERROR_DELETE_RATING, id, e);
            throw new DataAccessException(RATING_DELETE_ERROR, e);
        }
    }

    @NotNull
    private RatingRecord mapToRating(@NotNull ResultSet resultSet) throws SQLException {
        return new RatingRecord(
                resultSet.getString("id"),
                resultSet.getInt("rating"),
                resultSet.getString("comment"),
                resultSet.getTimestamp("date"),
                resultSet.getString("trip_id")
        );
    }
}
