package business.service;

import business.base.BookingService;
import com.man.constant.ErrorMessages;
import com.man.constant.LogMessages;
import data.dao.base.BookingDao;
import data.model.database.Booking;
import data.model.record.BookingRecord;
import exceptions.service.BookingServiceException;
import factory.DaoFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import presentation.context.CliContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса BookingService.
 * Предоставляет методы для работы с бронированиями, включая создание, получение, обновление и удаление.
 */
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    
    private final BookingDao bookingDao;

    public BookingServiceImpl() {
        this.bookingDao = DaoFactory.getBookingDao(CliContext.getCurrentStorageType());
    }

    @Override
    public String createBooking(@NotNull Booking booking, String tripId, String userId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_CREATION_START, tripId, userId);
        try {
            BookingRecord bookingRecord = new BookingRecord(booking, tripId, userId);
            String bookingId = bookingDao.createBooking(bookingRecord);
            log.info(LogMessages.BOOKING_CREATION_SUCCESS, bookingId);
            return bookingId;
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_CREATION_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(String bookingId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_SEARCH_BY_ID_START, bookingId);
        try {
            Optional<BookingRecord> bookingOptional = bookingDao.getBookingById(bookingId);
            if (bookingOptional.isEmpty()) {
                log.warn(LogMessages.BOOKING_SEARCH_BY_ID_ERROR, bookingId);
            }
            log.info(LogMessages.BOOKING_SEARCH_BY_ID_SUCCESS, bookingId);
            return bookingOptional.map(BookingRecord::toBooking);
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_SEARCH_BY_ID_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Booking> getAllBookings() throws BookingServiceException {
        log.info(LogMessages.BOOKING_GET_ALL_START);
        try {
            List<Booking> bookings = Collections.emptyList();
            log.info(LogMessages.BOOKING_GET_ALL_SUCCESS);
            return bookings;
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_GET_ALL_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_GET_ALL_ERROR, e);
        }
    }

    @Override
    public void updateBooking(@NotNull Booking booking, String tripId, String userId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_UPDATE_START, booking.getId());
        try {
            BookingRecord bookingRecord = new BookingRecord(booking, tripId, userId);
            bookingDao.updateBooking(bookingRecord);
            log.info(LogMessages.BOOKING_UPDATE_SUCCESS, booking.getId());
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_UPDATE_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteBooking(String bookingId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_DELETION_START, bookingId);
        try {
            bookingDao.deleteBooking(bookingId);
            log.info(LogMessages.BOOKING_DELETION_SUCCESS, bookingId);
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_DELETION_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_DELETION_ERROR, e);
        }
    }

    @Override
    public List<Booking> getBookingsByTrip(String tripId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_GET_BY_TRIP_START, tripId);
        try {
            throw new UnsupportedOperationException("Метод getBookingsByTrip не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_GET_BY_TRIP_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_GET_BY_TRIP_ERROR, e);
        }
    }

    @Override
    public List<Booking> getBookingsByUser(String userId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_GET_BY_USER_START, userId);
        try {
            throw new UnsupportedOperationException("Метод getBookingsByUser не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_GET_BY_USER_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_GET_BY_USER_ERROR, e);
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) throws BookingServiceException {
        log.info(LogMessages.BOOKING_GET_BY_STATUS_START, status);
        try {
            throw new UnsupportedOperationException("Метод getBookingsByStatus не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_GET_BY_STATUS_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_GET_BY_STATUS_ERROR, e);
        }
    }

    @Override
    public void cancelBooking(String bookingId) throws BookingServiceException {
        log.info(LogMessages.BOOKING_CANCEL_START, bookingId);
        try {
            throw new UnsupportedOperationException("Метод cancelBooking не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.BOOKING_CANCEL_ERROR, e.getMessage());
            throw new BookingServiceException(ErrorMessages.BOOKING_CANCEL_ERROR, e);
        }
    }
}