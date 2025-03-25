package com.carpooling.services.base;

import com.carpooling.entities.database.Rating;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.RatingServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с оценками.
 * Предоставляет методы для создания, получения, обновления и удаления оценок.
 */
public interface RatingService {

    /**
     * Создание новой оценки.
     *
     * @param rating Оценка для создания.
     * @return ID созданной оценки.
     * @throws RatingServiceException Если произошла ошибка при создании.
     */
    String createRating(Rating rating) throws RatingServiceException;

    /**
     * Получение оценки по ID.
     *
     * @param ratingId ID оценки.
     * @return Оценка, если найдена.
     * @throws RatingServiceException Если оценка не найдена или произошла ошибка.
     */
    Optional<Rating> getRatingById(String ratingId) throws RatingServiceException;

    /**
     * Получение всех оценок.
     *
     * @return Список всех оценок.
     * @throws RatingServiceException Если произошла ошибка при получении.
     */
    List<Rating> getAllRatings() throws RatingServiceException;

    /**
     * Обновление данных оценки.
     *
     * @param rating Оценка с обновленными данными.
     * @throws RatingServiceException Если произошла ошибка при обновлении.
     */
    void updateRating(Rating rating) throws RatingServiceException;

    /**
     * Удаление оценки по ID.
     *
     * @param ratingId ID оценки.
     * @throws RatingServiceException Если произошла ошибка при удалении.
     */
    void deleteRating(String ratingId) throws RatingServiceException;

    /**
     * Получение оценок по ID поездки.
     *
     * @param tripId ID поездки.
     * @return Список оценок для указанной поездки.
     * @throws RatingServiceException Если произошла ошибка при получении.
     */
    List<Rating> getRatingsByTrip(String tripId) throws RatingServiceException;

    /**
     * Получение оценок по рейтингу.
     *
     * @param rating Рейтинг оценки.
     * @return Список оценок с указанным рейтингом.
     * @throws RatingServiceException Если произошла ошибка при получении.
     */
    List<Rating> getRatingsByRating(int rating) throws RatingServiceException;

    /**
     * Получение средней оценки поездки.
     *
     * @param tripId ID поездки.
     * @return Средний рейтинг поездки.
     * @throws RatingServiceException Если произошла ошибка при расчете.
     */
    double getAverageRatingForTrip(String tripId) throws RatingServiceException;
}