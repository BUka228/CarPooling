package com.carpooling.services.base;

import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.TripException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления поездками.
 * Координирует создание поездок, маршрутов и связь с пользователями.
 */
public interface TripService {

    /**
     * Создает новую поездку.
     * Включает создание связанного маршрута (если он уникален для поездки).
     *
     * @param userId            ID пользователя-создателя.
     * @param startPoint        Начальная точка маршрута.
     * @param endPoint          Конечная точка маршрута.
     * @param departureDateTime Дата и время отправления.
     * @param maxPassengers     Максимальное количество пассажиров.
     * @return ID созданной поездки.
     * @throws TripException      Если произошла ошибка бизнес-логики (например, пользователь не найден).
     * @throws DataAccessException Если произошла ошибка доступа к данным при сохранении маршрута или поездки.
     */
    String createTrip(String userId, String startPoint, String endPoint,
                      LocalDateTime departureDateTime, byte maxPassengers)
            throws TripException, DataAccessException;

    /**
     * Получает поездку по ID.
     *
     * @param tripId ID поездки.
     * @return Optional с поездкой, если найдена.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    Optional<Trip> getTripById(String tripId) throws DataAccessException;

    // --- Методы-заглушки или для будущей реализации ---

    /**
     * Ищет поездки по заданным критериям (например, точки маршрута, дата).
     * ЗАГЛУШКА: Текущие DAO не поддерживают поиск.
     *
     * @param startPoint (Опционально) Начальная точка.
     * @param endPoint   (Опционально) Конечная точка.
     * @param date       (Опционально) Дата поездки.
     * @return Список найденных поездок (пустой список, если не поддерживается).
     * @throws OperationNotSupportedException Если поиск не поддерживается текущим хранилищем.
     * @throws DataAccessException          Если произошла ошибка доступа к данным.
     */
    List<Trip> findTrips(String startPoint, String endPoint, LocalDate date)
            throws OperationNotSupportedException, DataAccessException; // Добавили LocalDate

    /**
     * Отменяет поездку.
     * ЗАГЛУШКА: Требует логики изменения статуса и, возможно, уведомления пассажиров.
     *
     * @param tripId ID поездки для отмены.
     * @param userId ID пользователя, пытающегося отменить (для проверки прав).
     * @throws TripException      Если поездка не найдена, пользователь не имеет прав или отмена невозможна.
     * @throws OperationNotSupportedException Если обновление статуса не поддерживается.
     * @throws DataAccessException          Если произошла ошибка доступа к данным.
     */
    void cancelTrip(String tripId, String userId)
            throws TripException, OperationNotSupportedException, DataAccessException;

}