package com.carpooling.dao.base;

import com.carpooling.entities.record.BookingRecord;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * Интерфейс для работы с бронированиями.
 */
public interface BookingDao {
    /**
     * Создает новое бронирование.
     *
     * @param bookingRecord Бронирование для создания.
     * @return ID созданного бронирования.
     * @throws DataAccessException Если произошла ошибка при создании бронирования.
     */
    String createBooking(BookingRecord bookingRecord) throws DataAccessException;

    /**
     * Возвращает бронирование по ID.
     *
     * @param id ID бронирования.
     * @return Бронирование, если найдено.
     * @throws DataAccessException Если бронирование не найдено.
     */
    Optional<BookingRecord> getBookingById(String id) throws DataAccessException;

    /**
     * Обновляет бронирование.
     *
     * @param bookingRecord Бронирование для обновления.
     * @throws DataAccessException Если произошла ошибка при обновлении бронирования.
     */
    void updateBooking(BookingRecord bookingRecord) throws DataAccessException;

    /**
     * Удаляет бронирование.
     *
     * @param id ID бронирования.
     * @throws DataAccessException Если произошла ошибка при удалении бронирования.
     */
    void deleteBooking(String id) throws DataAccessException;
}