package com.carpooling.dao.base;

import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;

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
}