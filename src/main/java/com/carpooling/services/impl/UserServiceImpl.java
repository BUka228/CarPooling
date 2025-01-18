package com.carpooling.services.impl;

import com.carpooling.cli.context.CliContext;
import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.entities.record.UserRecord;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Реализация интерфейса UserService.
 * Предоставляет методы для работы с пользователями, включая регистрацию, авторизацию и управление данными.
 */
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = DaoFactory.getUserDao(CliContext.getCurrentStorageType());
    }


    @Override
    public String registerUser(@NotNull User user) throws UserServiceException {
        log.info(LogMessages.USER_REGISTRATION_START, user.getEmail());
        try {
            UserRecord userRecord = new UserRecord(user);
            String userId = userDao.createUser(userRecord);
            log.info(LogMessages.USER_REGISTRATION_SUCCESS, userId);
            return userId;
        } catch (Exception e) {
            log.error(LogMessages.USER_REGISTRATION_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_REGISTRATION_ERROR, e);
        }
    }

    @Override
    public Optional<User> getUserById(String userId) throws UserServiceException {
        log.info(LogMessages.USER_SEARCH_BY_ID_START, userId);
        try {
            Optional<UserRecord> userOptional = userDao.getUserById(userId);
            if (userOptional.isEmpty()) {
                log.warn(LogMessages.USER_SEARCH_BY_ID_ERROR, userId);
            }
            log.info(LogMessages.USER_SEARCH_BY_ID_SUCCESS, userId);
            return userOptional.map(UserRecord::toUser);
        } catch (Exception e) {
            log.error(LogMessages.USER_SEARCH_BY_ID_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_SEARCH_ERROR, e);
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) throws UserServiceException {
        log.info(LogMessages.USER_SEARCH_BY_EMAIL_START, email);
        try {
            // Реализация поиска по email (зависит от DAO)
            throw new UnsupportedOperationException("Метод getUserByEmail не реализован для текущего хранилища.");
        } catch (Exception e) {
            log.error(LogMessages.USER_SEARCH_BY_EMAIL_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_SEARCH_ERROR, e);
        }
    }

    @Override
    public void updateUser(@NotNull User user) throws UserServiceException {
        log.info(LogMessages.USER_UPDATE_START, user.getId());
        try {
            UserRecord userRecord = new UserRecord(user);
            userDao.updateUser(userRecord);
            log.info(LogMessages.USER_UPDATE_SUCCESS, user.getId());
        } catch (Exception e) {
            log.error(LogMessages.USER_UPDATE_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteUser(String userId) throws UserServiceException {
        log.info(LogMessages.USER_DELETION_START, userId);
        try {
            userDao.deleteUser(userId);
            log.info(LogMessages.USER_DELETION_SUCCESS, userId);
        } catch (Exception e) {
            log.error(LogMessages.USER_DELETION_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_DELETION_ERROR, e);
        }
    }

    @Override
    public Optional<User> authenticateUser(String email, String password) throws UserServiceException {
        log.info(LogMessages.USER_AUTHENTICATION_START, email);
        try {
            Optional<User> userOptional = getUserByEmail(email);
            if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
                log.info(LogMessages.USER_AUTHENTICATION_SUCCESS, email);
                return userOptional;
            } else {
                log.warn(LogMessages.USER_AUTHENTICATION_ERROR, email);
                throw new UserServiceException(ErrorMessages.USER_AUTHENTICATION_ERROR);
            }
        } catch (Exception e) {
            log.error(LogMessages.USER_AUTHENTICATION_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_AUTHENTICATION_ERROR, e);
        }
    }

    @Override
    public void changePassword(String userId, String newPassword) throws UserServiceException {
        log.info(LogMessages.USER_PASSWORD_CHANGE_START, userId);
        try {
            Optional<User> userOptional = getUserById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(newPassword);
                updateUser(user);
                log.info(LogMessages.USER_PASSWORD_CHANGE_SUCCESS, userId);
            } else {
                log.warn(LogMessages.USER_PASSWORD_CHANGE_ERROR, userId);
                throw new UserServiceException(ErrorMessages.USER_PASSWORD_CHANGE_ERROR);
            }
        } catch (Exception e) {
            log.error(LogMessages.USER_PASSWORD_CHANGE_ERROR, e.getMessage());
            throw new UserServiceException(ErrorMessages.USER_PASSWORD_CHANGE_ERROR, e);
        }
    }

    @Override
    public void blockUser(String userId) throws UserServiceException {
        log.info("Блокировка пользователя: {}", userId);
        throw new UnsupportedOperationException("Метод blockUser не реализован.");
    }

    @Override
    public void unblockUser(String userId) throws UserServiceException {
        log.info("Разблокировка пользователя: {}", userId);
        throw new UnsupportedOperationException("Метод unblockUser не реализован.");
    }
}
