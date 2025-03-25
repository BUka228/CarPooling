package com.carpooling.services.impl;

import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.Booking;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public String registerUser(@NotNull User user) throws UserServiceException {
        try {
            String userId = userDao.createUser(user);
            log.info("User registered successfully: {}", userId);
            return userId;
        } catch (DataAccessException e) {
            log.error("Error registering user: {}", e.getMessage());
            throw new UserServiceException("Error registering user", e);
        }
    }

    @Override
    public Optional<User> getUserById(String userId) throws UserServiceException {
        try {
            Optional<User> userOptional = userDao.getUserById(userId);
            if (userOptional.isPresent()) {
                log.info("User found: {}", userId);
            } else {
                log.warn("User not found: {}", userId);
            }
            return userOptional;
        } catch (DataAccessException e) {
            log.error("Error reading user: {}", e.getMessage());
            throw new UserServiceException("Error reading user", e);
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) throws UserServiceException {
        try {
            Optional<User> userOptional = Optional.empty();
            if (userOptional.isPresent()) {
                log.info("User found by email: {}", email);
            } else {
                log.warn("User not found by email: {}", email);
            }
            return userOptional;
        } catch (DataAccessException e) {
            log.error("Error reading user by email: {}", e.getMessage());
            throw new UserServiceException("Error reading user by email", e);
        }
    }

    @Override
    public void updateUser(@NotNull User user) throws UserServiceException {
        try {
            userDao.updateUser(user);
            log.info("User updated successfully: {}", user.getId());
        } catch (DataAccessException e) {
            log.error("Error updating user: {}", e.getMessage());
            throw new UserServiceException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(String userId) throws UserServiceException {
        try {
            userDao.deleteUser(userId);
            log.info("User deleted successfully: {}", userId);
        } catch (DataAccessException e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new UserServiceException("Error deleting user", e);
        }
    }

    @Override
    public Optional<User> authenticateUser(String email, String password) throws UserServiceException {
        try {
            Optional<User> userOptional = getUserByEmail(email);
            if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
                log.info("User authenticated successfully: {}", email);
                return userOptional;
            } else {
                log.warn("Authentication failed for user: {}", email);
                return Optional.empty();
            }
        } catch (UserServiceException e) {
            log.error("Error authenticating user: {}", e.getMessage());
            throw new UserServiceException("Error authenticating user", e);
        }
    }

    @Override
    public void changePassword(String userId, String newPassword) throws UserServiceException {
        try {
            Optional<User> userOptional = getUserById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(newPassword);
                updateUser(user);
                log.info("Password changed successfully for user: {}", userId);
            } else {
                log.warn("User not found for password change: {}", userId);
                throw new UserServiceException("User not found");
            }
        } catch (UserServiceException | DataAccessException e) {
            log.error("Error changing password for user: {}", e.getMessage());
            throw new UserServiceException("Error changing password", e);
        }
    }
}
