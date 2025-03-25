package com.carpooling.dao.base;

import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * Интерфейс для работы с пользователями в базе данных.
 */
public interface UserDao {
    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user Объект пользователя, который нужно создать.
     * @return ID созданного пользователя.
     * @throws DataAccessException Если произошла ошибка при создании пользователя.
     */
    String createUser(User user) throws DataAccessException;

    /**
     * Возвращает пользователя по его ID.
     *
     * @param id ID пользователя.
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional, если пользователь не найден.
     * @throws DataAccessException Если произошла ошибка при получении пользователя.
     */
    Optional<User> getUserById(String id) throws DataAccessException;

    /**
     * Обновляет информацию о пользователе в базе данных.
     *
     * @param user Объект пользователя с обновленными данными.
     * @throws DataAccessException Если произошла ошибка при обновлении пользователя.
     */
    void updateUser(User user) throws DataAccessException;

    /**
     * Удаляет пользователя из базы данных по его ID.
     *
     * @param id ID пользователя, которого нужно удалить.
     * @throws DataAccessException Если произошла ошибка при удалении пользователя.
     */
    void deleteUser(String id) throws DataAccessException;
}