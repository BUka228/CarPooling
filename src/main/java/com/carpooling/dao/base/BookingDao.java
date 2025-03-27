package com.carpooling.dao.base;

import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с бронированиями.
 */
public interface BookingDao {
    /**
     * Создает новое бронирование.
     *
     * @param booking Бронирование для создания.
     * @return ID созданного бронирования.
     * @throws DataAccessException Если произошла ошибка при создании бронирования.
     */
    String createBooking(Booking booking) throws DataAccessException;

    /**
     * Возвращает бронирование по ID.
     *
     * @param id ID бронирования.
     * @return Бронирование, если найдено.
     * @throws DataAccessException Если бронирование не найдено.
     */
    Optional<Booking> getBookingById(String id) throws DataAccessException;

    /**
     * Обновляет бронирование.
     *
     * @param booking Бронирование для обновления.
     * @throws DataAccessException Если произошла ошибка при обновлении бронирования.
     */
    void updateBooking(Booking booking) throws DataAccessException;

    /**
     * Удаляет бронирование.
     *
     * @param id ID бронирования.
     * @throws DataAccessException Если произошла ошибка при удалении бронирования.
     */
    void deleteBooking(String id) throws DataAccessException;

    /**
     * Подсчитывает количество уже забронированных мест на конкретную поездку.
     * @param tripId ID поездки.
     * @return Количество забронированных мест (0, если нет бронирований или ошибка).
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     * @throws OperationNotSupportedException Если операция не поддерживается.
     */
    int countBookedSeatsForTrip(String tripId) throws DataAccessException, OperationNotSupportedException;

    /**
     * Находит бронирования по ID пользователя.
     * @param userId ID пользователя.
     * @return Список бронирований пользователя.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     * @throws OperationNotSupportedException Если операция не поддерживается.
     */
    List<Booking> findBookingsByUserId(String userId) throws DataAccessException, OperationNotSupportedException;

    /**
     * Находит бронирование по пользователю и поездке (для проверки участия/повторного бронирования).
     * @param userId ID пользователя.
     * @param tripId ID поездки.
     * @return Optional с бронированием, если найдено.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     * @throws OperationNotSupportedException Если операция не поддерживается.
     */
    Optional<Booking> findBookingByUserAndTrip(String userId, String tripId) throws DataAccessException, OperationNotSupportedException;
}