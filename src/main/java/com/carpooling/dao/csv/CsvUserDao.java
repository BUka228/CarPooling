package com.carpooling.dao.csv;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CsvUserDao extends AbstractCsvDao<User> implements UserDao {

    public CsvUserDao(String filePath) {super(User.class, filePath);}

    @Override
    public String createUser(@NotNull User user) throws DataAccessException {
        UUID userId = generateId();
        user.setId(userId);
        try {
            List<User> users = readAll();
            users.add(user);
            writeAll(users);
            log.info("User created successfully: {}", userId);
            return userId.toString();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public Optional<User> getUserById(String id) throws DataAccessException {
        try {
            Optional<User> user = findById(record -> record.getId().toString().equals(id));
            if (user.isPresent()) {
                log.info("User found: {}", id);
            } else {
                log.warn("User not found: {}", id);
            }
            return user;
        } catch (IOException e) {
            log.error("Error reading user: {}", e.getMessage());
            throw new DataAccessException("Error reading user", e);
        }
    }

    @Override
    public void updateUser(@NotNull User user) throws DataAccessException {
        try {
            List<User> users = readAll();
            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.warn("User not found for update: {}", user.getId());
                throw new DataAccessException("User not found");
            }
            writeAll(users);
            log.info("User updated successfully: {}", user.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error updating user: {}", e.getMessage());
            throw new DataAccessException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try {
            boolean found = deleteById(record -> record.getId().toString().equals(id));
            if (!found) {
                log.warn("User not found for deletion: {}", id);
            } else {
                log.info("User deleted successfully: {}", id);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new DataAccessException("Error deleting user", e);
        }
    }
}