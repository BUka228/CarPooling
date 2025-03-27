package com.carpooling.services.base;


import com.carpooling.entities.database.Booking;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.BookingException;
import com.carpooling.exceptions.service.OperationNotSupportedException;

import java.time.LocalDate;
import java.util.List; // Для будущих методов
import java.util.Optional;

/**
 * Сервис для управления бронированиями.
 */
public interface BookingService {

    /**
     * Создает новое бронирование для пользователя на определенную поездку.
     * Должен включать проверку доступности мест (сейчас заглушка).
     *
     * @param userId           ID пользователя, который бронирует.
     * @param tripId           ID поездки.
     * @param numberOfSeats    Количество бронируемых мест.
     * @param passportNumber   Номер паспорта (если требуется).
     * @param passportExpiry   Дата окончания срока действия паспорта (если требуется).
     * @return ID созданного бронирования.
     * @throws BookingException      Если произошла ошибка (поездка/пользователь не найдены, нет мест).
     * @throws OperationNotSupportedException Если проверка доступности мест не поддерживается.
     * @throws DataAccessException   Если произошла ошибка доступа к данным.
     */
    String createBooking(String userId, String tripId, byte numberOfSeats, String passportNumber, LocalDate passportExpiry)
            throws BookingException, OperationNotSupportedException, DataAccessException;

    /**
     * Получает бронирование по ID.
     *
     * @param bookingId ID бронирования.
     * @return Optional с бронированием, если найдено.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    Optional<Booking> getBookingById(String bookingId) throws DataAccessException;

    /**
     * Отменяет бронирование.
     * ЗАГЛУШКА: Требует логики изменения статуса.
     *
     * @param bookingId ID бронирования.
     * @param userId    ID пользователя, пытающегося отменить (для проверки прав).
     * @throws BookingException Если бронирование не найдено, пользователь не имеет прав или отмена невозможна.
     * @throws OperationNotSupportedException Если обновление статуса не поддерживается.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    void cancelBooking(String bookingId, String userId)
            throws BookingException, OperationNotSupportedException, DataAccessException;

    /**
     * Получает список бронирований для конкретного пользователя.
     * ЗАГЛУШКА: Требует метода поиска в DAO.
     *
     * @param userId ID пользователя.
     * @return Список бронирований (пустой, если не поддерживается).
     * @throws OperationNotSupportedException Если поиск не поддерживается.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    List<Booking> findBookingsByUserId(String userId)
            throws OperationNotSupportedException, DataAccessException;

}
