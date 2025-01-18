package com.carpooling.dao.postgres;


import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.record.RouteRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.ROUTE_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.ROUTE_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;
import static com.carpooling.constants.Constants.*;


@Slf4j
public class PostgresRouteDao extends AbstractPostgresDao implements RouteDao {

    public PostgresRouteDao(Connection connection) {
        super(connection);
    }

    @Override
    public String createRoute(@NotNull RouteRecord routeRecord) throws DataAccessException {
        String routeId = generateId();

        try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE_ROUTE)) {
            statement.setObject(1, stringToUUID(routeId));
            statement.setString(2, routeRecord.getStartPoint());
            statement.setString(3, routeRecord.getEndPoint());
            statement.setTimestamp(4, new Timestamp(routeRecord.getDate().getTime()));
            statement.setShort(5, routeRecord.getEstimatedDuration());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                log.info(CREATE_ROUTE_SUCCESS, routeId);
                return routeId;
            } else {
                log.error(ERROR_CREATE_ROUTE, routeRecord.getStartPoint(), routeRecord.getEndPoint());
                throw new DataAccessException(ROUTE_CREATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ERROR_CREATE_ROUTE, routeRecord.getStartPoint(), routeRecord.getEndPoint(), e);
            throw new DataAccessException(ROUTE_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RouteRecord> getRouteById(String id) throws DataAccessException {

        try (PreparedStatement statement = connection.prepareStatement(SQL_GET_ROUTE_BY_ID)) {
            statement.setObject(1, stringToUUID(id));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(GET_ROUTE_START, id);
                return Optional.of(mapToRoute(resultSet));
            } else {
                log.warn(WARN_ROUTE_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(ERROR_GET_ROUTE, id, e);
            throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRoute(@NotNull RouteRecord routeRecord) throws DataAccessException {

        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_ROUTE)) {
            statement.setString(1, routeRecord.getStartPoint());
            statement.setString(2, routeRecord.getEndPoint());
            statement.setTimestamp(3, new Timestamp(routeRecord.getDate().getTime()));
            statement.setShort(4, routeRecord.getEstimatedDuration());
            statement.setObject(5, stringToUUID(routeRecord.getId()));

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                log.info(UPDATE_ROUTE_SUCCESS, routeRecord.getId());
            } else {
                log.warn(WARN_ROUTE_NOT_FOUND, routeRecord.getId());
                throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, routeRecord.getId()));
            }
        } catch (SQLException e) {
            log.error(ERROR_UPDATE_ROUTE, routeRecord.getId(), e);
            throw new DataAccessException(ROUTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {

        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_ROUTE)) {
            statement.setObject(1, stringToUUID(id));

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info(DELETE_ROUTE_SUCCESS, id);
            } else {
                log.warn(WARN_ROUTE_NOT_FOUND, id);
                throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, id));
            }
        } catch (SQLException e) {
            log.error(ERROR_DELETE_ROUTE, id, e);
            throw new DataAccessException(ROUTE_DELETE_ERROR, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    private RouteRecord mapToRoute(@NotNull ResultSet resultSet) throws SQLException {
        return new RouteRecord(
                resultSet.getString("id"),
                resultSet.getString("start_point"),
                resultSet.getString("end_point"),
                resultSet.getTimestamp("date"),
                resultSet.getShort("estimated_duration")
        );
    }
}

