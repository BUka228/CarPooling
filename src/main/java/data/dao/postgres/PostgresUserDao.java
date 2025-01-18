package data.dao.postgres;

import data.dao.base.UserDao;
import data.model.record.UserRecord;
import exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static com.man.constant.Constants.*;
import static com.man.constant.ErrorMessages.USER_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;

@Slf4j
public class PostgresUserDao extends AbstractPostgresDao implements UserDao {

    public PostgresUserDao(Connection connection) {
        super(connection);
    }

    @Override
    public String createUser(@NotNull UserRecord userRecord) throws DataAccessException {
        String userId = generateId();

        try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_SQL)) {
            statement.setObject(1, stringToUUID(userId));
            statement.setString(2, userRecord.getName());
            statement.setString(3, userRecord.getEmail());
            statement.setString(4, userRecord.getPassword());
            statement.setString(5, userRecord.getGender());
            statement.setString(6, userRecord.getPhone());
            statement.setDate(7, new java.sql.Date(userRecord.getBirthDate().getTime()));
            statement.setString(8, userRecord.getAddress());
            statement.setString(9, userRecord.getPreferences());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                log.info(CREATE_USER_SUCCESS, userId);
                return userId;
            } else {
                log.error(ERROR_CREATE_USER, userRecord.getName());
                throw new DataAccessException(USER_CREATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ERROR_CREATE_USER, userRecord.getName(), e);
            throw new DataAccessException(USER_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<UserRecord> getUserById(String id) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID_SQL)) {
            statement.setObject(1, stringToUUID(id));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(GET_USER_START, id);
                return Optional.of(mapToUser(resultSet));
            } else {
                log.warn(WARN_USER_NOT_FOUND, id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error(ERROR_GET_USER, id, e);
            throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateUser(@NotNull UserRecord userRecord) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            statement.setString(1, userRecord.getName());
            statement.setString(2, userRecord.getEmail());
            statement.setString(3, userRecord.getPassword());
            statement.setString(4, userRecord.getGender());
            statement.setString(5, userRecord.getPhone());
            statement.setDate(6, new java.sql.Date(userRecord.getBirthDate().getTime()));
            statement.setString(7, userRecord.getAddress());
            statement.setString(8, userRecord.getPreferences());
            statement.setObject(9, stringToUUID(userRecord.getId()));


            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                log.info(UPDATE_USER_SUCCESS, userRecord.getId());
            } else {
                log.warn(WARN_USER_NOT_FOUND, userRecord.getId());
                throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, userRecord.getId()));
            }
        } catch (SQLException e) {
            log.error(ERROR_UPDATE_USER, userRecord.getId(), e);
            throw new DataAccessException(USER_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            statement.setObject(1, stringToUUID(id));

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                log.info(DELETE_USER_SUCCESS, id);
            } else {
                log.warn(WARN_USER_NOT_FOUND, id);
                throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, id));
            }
        } catch (SQLException e) {
            log.error(ERROR_DELETE_USER, id, e);
            throw new DataAccessException(USER_DELETE_ERROR, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    private UserRecord mapToUser(@NotNull ResultSet resultSet) throws SQLException {
        return new UserRecord(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("gender"),
                resultSet.getString("phone"),
                resultSet.getDate("birth_date"),
                resultSet.getString("address"),
                resultSet.getString("preferences")
        );
    }
}

