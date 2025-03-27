package com.carpooling.services.base;

import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.RatingException;

import java.util.List; // Для будущих методов
import java.util.Optional;

/**
 * Сервис для управления оценками поездок.
 */
public interface RatingService {

    /**
     * Создает новую оценку для поездки.
     * Может включать проверку, что пользователь участвовал в поездке (заглушка).
     *
     * @param userId  ID пользователя, оставляющего оценку.
     * @param tripId  ID оцениваемой поездки.
     * @param ratingValue Значение рейтинга (например, от 1 до 5).
     * @param comment Комментарий (опционально).
     * @return ID созданной оценки.
     * @throws RatingException Если произошла ошибка (поездка/пользователь не найдены, невалидный рейтинг).
     * @throws OperationNotSupportedException Если связанные проверки не поддерживаются.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    String createRating(String userId, String tripId, int ratingValue, String comment)
            throws RatingException, OperationNotSupportedException, DataAccessException;

    /**
     * Получает оценку по ID.
     *
     * @param ratingId ID оценки.
     * @return Optional с оценкой, если найдена.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    Optional<Rating> getRatingById(String ratingId) throws DataAccessException;

    /**
     * Получает все оценки для конкретной поездки.
     * ЗАГЛУШКА: Требует метода поиска в DAO.
     *
     * @param tripId ID поездки.
     * @return Список оценок (пустой, если не поддерживается).
     * @throws OperationNotSupportedException Если поиск не поддерживается.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    List<Rating> findRatingsByTripId(String tripId)
            throws OperationNotSupportedException, DataAccessException;

}
