package com.carpooling.dao.xml;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.entities.record.BookingRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.BOOKING_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.BOOKING_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;



@Slf4j
public class XmlBookingDao extends AbstractXmlDao<BookingRecord, XmlBookingDao.BookingWrapper> implements BookingDao {

    public XmlBookingDao(String filePath) {
        super(BookingRecord.class, BookingWrapper.class, filePath);
    }

    @Override
    public String createBooking(@NotNull BookingRecord bookingRecord) throws DataAccessException {
        log.info(CREATE_BOOKING_START, bookingRecord.getTripId(), bookingRecord.getUserId());

        // Генерация UUID для ID
        String bookingId = generateId();
        bookingRecord.setId(bookingId);

        try {
            List<BookingRecord> bookings = readAll();
            bookings.add(bookingRecord);
            writeAll(bookings);

            log.info(CREATE_BOOKING_SUCCESS, bookingId);
            return bookingId;
        } catch (JAXBException e) {
            log.error(ERROR_CREATE_BOOKING, bookingRecord.getTripId(), bookingRecord.getUserId(), e);
            throw new DataAccessException(BOOKING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<BookingRecord> getBookingById(String id) throws DataAccessException {
        log.info(GET_BOOKING_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (JAXBException e) {
            log.error(ERROR_GET_BOOKING, id, e);
            throw new DataAccessException(String.format(BOOKING_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateBooking(BookingRecord bookingRecord) throws DataAccessException {
        try {
            List<BookingRecord> bookings = readAll();
            boolean updated = updateItem(bookings, record -> record.getId().equals(bookingRecord.getId()), bookingRecord);

            if (!updated) {
                log.warn(WARN_BOOKING_NOT_FOUND, bookingRecord.getId());
                throw new DataAccessException(String.format(BOOKING_NOT_FOUND_ERROR, bookingRecord.getId()));
            }

            writeAll(bookings);
            log.info(UPDATE_BOOKING_SUCCESS, bookingRecord.getId());
        } catch (JAXBException e) {
            log.error(ERROR_UPDATE_BOOKING, bookingRecord.getId(), e);
            throw new DataAccessException(BOOKING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteBooking(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_BOOKING_SUCCESS, id);
        } catch (JAXBException e) {
            log.error(ERROR_DELETE_BOOKING, id, e);
            throw new DataAccessException(BOOKING_DELETE_ERROR, e);
        }
    }

    @Override
    protected List<BookingRecord> getItemsFromWrapper(@NotNull BookingWrapper wrapper) {
        return wrapper.getBookings();
    }

    @Override
    protected BookingWrapper createWrapper(List<BookingRecord> items) {
        return new BookingWrapper(items);
    }

    /**
     * Вспомогательный класс для обертки списка бронирований в XML.
     */
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "bookings")
    protected static class BookingWrapper {
        private List<BookingRecord> bookings;

        @XmlElement(name = "booking")
        public List<BookingRecord> getBookings() {
            return bookings;
        }
    }
}