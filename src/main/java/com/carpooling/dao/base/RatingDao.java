package com.carpooling.dao.base;

import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;

import java.util.List;
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

    /**
     * Находит оценки по ID поездки.
     * @param tripId ID поездки.
     * @return Список оценок для поездки.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     * @throws OperationNotSupportedException Если операция не поддерживается.
     */
    List<Rating> findRatingsByTripId(String tripId) throws DataAccessException, OperationNotSupportedException;

    /**
     * Находит оценку по пользователю и поездке (для проверки повторной оценки).
     * ПРЕДПОЛАГАЕТСЯ, что сущность Rating имеет связь ManyToOne с User.
     * Если связи нет, этот метод не имеет смысла в RatingDao.
     * @param userId ID пользователя, оставившего оценку.
     * @param tripId ID поездки.
     * @return Optional с оценкой, если найдена.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     * @throws OperationNotSupportedException Если операция не поддерживается (или если связь с User отсутствует).
     */
    Optional<Rating> findRatingByUserAndTrip(String userId, String tripId) throws DataAccessException, OperationNotSupportedException;

}
