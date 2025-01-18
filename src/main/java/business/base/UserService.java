package business.base;

import data.model.database.User;
import exceptions.service.UserServiceException;

import java.util.Optional;

/**
 * Интерфейс для работы с пользователями.
 * Предоставляет методы для регистрации, авторизации, получения и обновления данных пользователя.
 */
public interface UserService {

    /**
     * Регистрация нового пользователя.
     *
     * @param user Пользователь для регистрации.
     * @return ID зарегистрированного пользователя.
     * @throws UserServiceException Если произошла ошибка при регистрации.
     */
    String registerUser(User user) throws UserServiceException;

    /**
     * Получение пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return Пользователь, если найден.
     * @throws UserServiceException Если пользователь не найден или произошла ошибка.
     */
    Optional<User> getUserById(String userId) throws UserServiceException;

    /**
     * Получение пользователя по email.
     *
     * @param email Email пользователя.
     * @return Пользователь, если найден.
     * @throws UserServiceException Если пользователь не найден или произошла ошибка.
     */
    Optional<User> getUserByEmail(String email) throws UserServiceException;

    /**
     * Обновление данных пользователя.
     *
     * @param user Пользователь с обновленными данными.
     * @throws UserServiceException Если произошла ошибка при обновлении.
     */
    void updateUser(User user) throws UserServiceException;

    /**
     * Удаление пользователя по ID.
     *
     * @param userId ID пользователя.
     * @throws UserServiceException Если произошла ошибка при удалении.
     */
    void deleteUser(String userId) throws UserServiceException;

    /**
     * Аутентификация пользователя по email и паролю.
     *
     * @param email    Email пользователя.
     * @param password Пароль пользователя.
     * @return Пользователь, если аутентификация успешна.
     * @throws UserServiceException Если аутентификация не удалась или произошла ошибка.
     */
    Optional<User> authenticateUser(String email, String password) throws UserServiceException;

    /**
     * Изменение пароля пользователя.
     *
     * @param userId      ID пользователя.
     * @param newPassword Новый пароль.
     * @throws UserServiceException Если произошла ошибка при изменении пароля.
     */
    void changePassword(String userId, String newPassword) throws UserServiceException;

    /**
     * Блокировка пользователя.
     *
     * @param userId ID пользователя.
     * @throws UserServiceException Если произошла ошибка при блокировке.
     */
    void blockUser(String userId) throws UserServiceException;

    /**
     * Разблокировка пользователя.
     *
     * @param userId ID пользователя.
     * @throws UserServiceException Если произошла ошибка при разблокировке.
     */
    void unblockUser(String userId) throws UserServiceException;
}
