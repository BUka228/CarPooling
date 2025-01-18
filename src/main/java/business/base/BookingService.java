package business.base;

import data.model.database.Booking;
import exceptions.service.BookingServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с бронированиями.
 * Предоставляет методы для создания, получения, обновления и удаления бронирований.
 */
public interface BookingService {

    /**
     * Создание нового бронирования.
     *
     * @param booking Бронирование для создания.
     * @param tripId  ID поездки, к которой относится бронирование.
     * @param userId  ID пользователя, создающего бронирование.
     * @return ID созданного бронирования.
     * @throws BookingServiceException Если произошла ошибка при создании.
     */
    String createBooking(Booking booking, String tripId, String userId) throws BookingServiceException;

    /**
     * Получение бронирования по ID.
     *
     * @param bookingId ID бронирования.
     * @return Бронирование, если найдено.
     * @throws BookingServiceException Если бронирование не найдено или произошла ошибка.
     */
    Optional<Booking> getBookingById(String bookingId) throws BookingServiceException;

    /**
     * Получение всех бронирований.
     *
     * @return Список всех бронирований.
     * @throws BookingServiceException Если произошла ошибка при получении.
     */
    List<Booking> getAllBookings() throws BookingServiceException;

    /**
     * Обновление данных бронирования.
     *
     * @param booking Бронирование с обновленными данными.
     * @param tripId  ID поездки, к которой относится бронирование.
     * @param userId  ID пользователя.
     * @throws BookingServiceException Если произошла ошибка при обновлении.
     */
    void updateBooking(Booking booking, String tripId, String userId) throws BookingServiceException;

    /**
     * Удаление бронирования по ID.
     *
     * @param bookingId ID бронирования.
     * @throws BookingServiceException Если произошла ошибка при удалении.
     */
    void deleteBooking(String bookingId) throws BookingServiceException;

    /**
     * Получение бронирований по ID поездки.
     *
     * @param tripId ID поездки.
     * @return Список бронирований для указанной поездки.
     * @throws BookingServiceException Если произошла ошибка при получении.
     */
    List<Booking> getBookingsByTrip(String tripId) throws BookingServiceException;

    /**
     * Получение бронирований по ID пользователя.
     *
     * @param userId ID пользователя.
     * @return Список бронирований для указанного пользователя.
     * @throws BookingServiceException Если произошла ошибка при получении.
     */
    List<Booking> getBookingsByUser(String userId) throws BookingServiceException;

    /**
     * Получение бронирований по статусу.
     *
     * @param status Статус бронирования.
     * @return Список бронирований с указанным статусом.
     * @throws BookingServiceException Если произошла ошибка при получении.
     */
    List<Booking> getBookingsByStatus(String status) throws BookingServiceException;

    /**
     * Отмена бронирования.
     *
     * @param bookingId ID бронирования.
     * @throws BookingServiceException Если произошла ошибка при отмене.
     */
    void cancelBooking(String bookingId) throws BookingServiceException;
}