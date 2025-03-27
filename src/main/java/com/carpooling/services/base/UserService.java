package com.carpooling.services.base;

import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.AuthenticationException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.RegistrationException;

import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Содержит бизнес-логику, связанную с пользователями.
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя.
     * Может включать проверку уникальности email (если DAO поддерживает).
     * Должен включать хеширование пароля перед сохранением.
     *
     * @param user Объект User с данными для регистрации (пароль в открытом виде).
     * @return ID созданного пользователя.
     * @throws RegistrationException Если регистрация не удалась (например, email занят).
     * @throws DataAccessException   Если произошла ошибка доступа к данным.
     */
    String registerUser(User user) throws RegistrationException, DataAccessException;

    /**
     * Аутентифицирует пользователя по email и паролю.
     * Сравнивает предоставленный пароль с хешем в хранилище.
     *
     * @param email    Email пользователя.
     * @param password Пароль пользователя (в открытом виде).
     * @return Объект User в случае успеха.
     * @throws AuthenticationException      Если email не найден или пароль неверный.
     * @throws OperationNotSupportedException Если текущее хранилище не поддерживает поиск по email.
     * @throws DataAccessException          Если произошла ошибка доступа к данным.
     */
    User loginUser(String email, String password) throws AuthenticationException, OperationNotSupportedException, DataAccessException;

    /**
     * Получает пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return Optional с пользователем, если найден.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    Optional<User> getUserById(String userId) throws DataAccessException;

    // Можно добавить другие методы, например:
    // void updateUserProfile(User user) throws UserNotFoundException, DataAccessException;
    // void changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException, UserNotFoundException, DataAccessException;
}