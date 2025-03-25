package com.carpooling.dao.base;

import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * Интерфейс для работы с рейтингами.
 */
public interface RatingDao {
    /**
     * Создает новый рейтинг.
     * @param rating Объект рейтинга, содержащий информацию о рейтинге.
     * @return ID созданного рейтинга.
     * @throws DataAccessException Если произошла ошибка при создании рейтинга.
     */
    String createRating(Rating rating) throws DataAccessException;
    /**
     * Возвращает рейтинг по его ID.
     * @param id ID рейтинга.
     * @return Объект рейтинга.
     * @throws DataAccessException Если рейтинг не найден.
     */
    Optional<Rating> getRatingById(String id) throws DataAccessException;
    /**
     * Обновляет существующий рейтинг.
     * @param rating Объект рейтинга с обновленными данными.
     * @throws DataAccessException Если произошла ошибка при обновлении рейтинга.
     */
    void updateRating(Rating rating) throws DataAccessException;
    /**
     * Удаляет рейтинг по его ID.
     * @param id ID рейтинга.
     * @throws DataAccessException Если произошла ошибка при удалении рейтинга.
     */
    void deleteRating(String id) throws DataAccessException;
}
