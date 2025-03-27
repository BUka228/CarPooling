package com.carpooling.services.impl;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.AuthenticationException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.RegistrationException;
import com.carpooling.services.base.UserService;
import com.carpooling.transaction.DataAccessManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
// import org.springframework.security.crypto.password.PasswordEncoder; // Если используется

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final DataAccessManager dataAccessManager;
    // private final PasswordEncoder passwordEncoder; // Внедрить или создать

    // Конструктор для DI
    public UserServiceImpl(UserDao userDao, DataAccessManager dataAccessManager /*, PasswordEncoder passwordEncoder */) {
        this.userDao = userDao;
        this.dataAccessManager = dataAccessManager;
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String registerUser(User user) throws RegistrationException, DataAccessException {
        log.debug("Attempting to register user with email: {}", user.getEmail());

        // --- Валидация входных данных (вне транзакции) ---
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RegistrationException("Password cannot be empty.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new RegistrationException("Invalid email format.");
        }
        // Другие валидации...

        // --- Хеширование пароля (вне транзакции) ---
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.warn("User registration: Password for user {} is NOT hashed.", user.getEmail());

        // --- Операции с БД (в транзакции) ---
        try {
            String userId = dataAccessManager.executeInTransaction(() -> {
                // 1. Проверка email на уникальность
                try {
                    if (userDao.findByEmail(user.getEmail()).isPresent()) {
                        throw new RegistrationException("Email '" + user.getEmail() + "' уже используется.");
                    }
                } catch (OperationNotSupportedException e) {
                    log.warn("DAO {} does not support findByEmail, skipping uniqueness check.", userDao.getClass().getSimpleName());
                }
                // DataAccessException из findByEmail будет поймана ниже

                // 2. Создание пользователя
                return userDao.createUser(user);
            });

            log.info("User registered successfully with ID: {} and email: {}", userId, user.getEmail());
            return userId;

        } catch (DataAccessException e) {
            log.error("Data access error during user registration for email {}: {}", user.getEmail(), e.getMessage(), e);
            handleRegistrationDbError(e, user.getEmail()); // Вынесли обработку ошибок БД
            throw e; // Если не обработали специфично, перебрасываем
        } catch (Exception e) { // Ловим другие ошибки из лямбды (если wrapException их пропустил)
            log.error("Unexpected error during user registration for email {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RegistrationException("Непредвиденная ошибка при регистрации.", e);
        }
    }

    @Override
    public User loginUser(String email, String password) throws AuthenticationException, DataAccessException {
        log.debug("Attempting to login user with email: {}", email);

        // Чтение пользователя (используем read-only)
        Optional<User> userOpt = dataAccessManager.executeReadOnly(() ->
                userDao.findByEmail(email)
        );

        User user = userOpt.orElseThrow(() -> {
            log.warn("Login failed: User not found for email {}", email);
            return new AuthenticationException("Пользователь с email '" + email + "' не найден.");
        });

        // --- Проверка пароля (ВАЖНО: использовать passwordEncoder.matches) ---
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     log.warn("Login failed: Invalid password for user {}", email);
        //     throw new AuthenticationException("Неверный пароль.");
        // }
        // --- ЗАГЛУШКА ---
        if (!user.getPassword().equals(password)) {
            log.warn("Login failed: Invalid password for user {} (using direct comparison - UNSAFE!)", email);
            throw new AuthenticationException("Неверный пароль.");
        }
        log.warn("Login check for user {} uses direct password comparison - UNSAFE!", email);
        // --- КОНЕЦ ЗАГЛУШКИ ---


        log.info("User {} authenticated successfully.", email);
        return user;
    }

    @Override
    public Optional<User> getUserById(String userId) throws DataAccessException {
        log.debug("Fetching user by ID: {}", userId);
        return dataAccessManager.executeReadOnly(() ->
                userDao.getUserById(userId)
        );
    }

    // --- Приватные хелперы ---

    // Обработка ошибок БД при регистрации
    private void handleRegistrationDbError(DataAccessException e, String email) throws RegistrationException {
        Throwable cause = e.getCause();
        while (cause != null && !(cause instanceof ConstraintViolationException)) {
            cause = cause.getCause();
        }
        if (cause instanceof ConstraintViolationException cve) {
            SQLException sqlEx = cve.getSQLException();
            String sqlState = (sqlEx != null) ? sqlEx.getSQLState() : null;
            if ("23505".equals(sqlState)) { // PostgreSQL unique violation
                log.warn("Registration failed for email {}: Constraint violation (SQLState: {} - likely email exists).", email, sqlState);
                throw new RegistrationException("Email '" + email + "' уже используется.", e);
            } else {
                log.warn("Registration failed for email {}: Constraint violation (SQLState: {}, Name: {}).", email, sqlState, cve.getConstraintName());
                throw new RegistrationException("Ошибка данных при регистрации (нарушение ограничения).", e);
            }
        }
        // Если не ConstraintViolation, ничего не делаем, ошибка будет переброшена выше
    }

    // --- Методы для update/delete (как раньше, с dataAccessManager) ---
    public void updateUserProfile(User user) throws DataAccessException {
        // Валидация user...
        log.debug("Attempting to update profile for user ID: {}", user.getId());
        dataAccessManager.executeInTransaction(() -> {
            userDao.updateUser(user);
            return null;
        });
        log.info("User profile updated successfully for ID: {}", user.getId());
    }

    public void deleteUserAccount(String userId) throws DataAccessException {
        log.debug("Attempting to delete account for user ID: {}", userId);
        dataAccessManager.executeInTransaction(() -> {
            userDao.deleteUser(userId);
            // Проверка существования может быть добавлена здесь, если DAO не бросает исключение
            return null;
        });
        log.info("Delete operation attempted for user ID: {}", userId);
    }
}