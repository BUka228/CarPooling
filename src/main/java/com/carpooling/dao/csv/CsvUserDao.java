package com.carpooling.dao.csv;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.record.UserRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.USER_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class CsvUserDao extends AbstractCsvDao<UserRecord> implements UserDao {

    public CsvUserDao(String filePath) {
        super(UserRecord.class, filePath);
    }

    @Override
    public String createUser(@NotNull UserRecord userRecord) throws DataAccessException {
        log.info(CREATE_USER_START, userRecord.getName());

        // Генерация UUID для ID
        String userId = generateId();
        userRecord.setId(userId);

        try {
            List<UserRecord> users = readAll();
            users.add(userRecord);
            writeAll(users);

            log.info(CREATE_USER_SUCCESS, userId);
            return userId;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_CREATE_USER, userRecord.getName(), e);
            throw new DataAccessException(USER_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<UserRecord> getUserById(String id) throws DataAccessException {
        log.info(GET_USER_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (IOException e) {
            log.error(ERROR_GET_USER, id, e);
            throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateUser(@NotNull UserRecord userRecord) throws DataAccessException {
        try {
            List<UserRecord> users = readAll();
            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(userRecord.getId())) {
                    users.set(i, userRecord);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.warn(WARN_USER_NOT_FOUND, userRecord.getId());
                throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, userRecord.getId()));
            }
            writeAll(users);
            log.info(UPDATE_USER_SUCCESS, userRecord.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_UPDATE_USER, userRecord.getId(), e);
            throw new DataAccessException(USER_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_USER_SUCCESS, id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_DELETE_USER, id, e);
            throw new DataAccessException(USER_DELETE_ERROR, e);
        }
    }
}