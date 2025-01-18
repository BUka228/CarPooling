package data.dao.xml;

import data.dao.base.TripDao;
import data.model.record.TripRecord;
import exceptions.dao.DataAccessException;
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

import static com.man.constant.ErrorMessages.TRIP_CREATION_ERROR;
import static com.man.constant.ErrorMessages.TRIP_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;

@Slf4j
public class XmlTripDao extends AbstractXmlDao<TripRecord, XmlTripDao.TripWrapper> implements TripDao {

    public XmlTripDao(String filePath) {
        super(TripRecord.class, TripWrapper.class, filePath);
    }

    @Override
    public String createTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        log.info(CREATE_TRIP_START, tripRecord.getUserId(), tripRecord.getRouteId());

        // Генерация UUID для ID
        String tripId = generateId();
        tripRecord.setId(tripId);

        try {
            List<TripRecord> trips = readAll();
            trips.add(tripRecord);
            writeAll(trips);

            log.info(CREATE_TRIP_SUCCESS, tripId);
            return tripId;
        } catch (JAXBException e) {
            log.error(ERROR_CREATE_TRIP, tripRecord.getUserId(), tripRecord.getRouteId(), e);
            throw new DataAccessException(TRIP_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<TripRecord> getTripById(String id) throws DataAccessException {
        log.info(GET_TRIP_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (JAXBException e) {
            log.error(ERROR_GET_TRIP, id, e);
            throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateTrip(@NotNull TripRecord tripRecord) throws DataAccessException {
        try {
            List<TripRecord> trips = readAll();
            boolean updated = updateItem(trips, record -> record.getId().equals(tripRecord.getId()), tripRecord);

            if (!updated) {
                log.warn(WARN_TRIP_NOT_FOUND, tripRecord.getId());
                throw new DataAccessException(String.format(TRIP_NOT_FOUND_ERROR, tripRecord.getId()));
            }

            writeAll(trips);
            log.info(UPDATE_TRIP_SUCCESS, tripRecord.getId());
        } catch (JAXBException e) {
            log.error(ERROR_UPDATE_TRIP, tripRecord.getId(), e);
            throw new DataAccessException(TRIP_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_TRIP_SUCCESS, id);
        } catch (JAXBException e) {
            log.error(ERROR_DELETE_TRIP, id, e);
            throw new DataAccessException(TRIP_DELETE_ERROR, e);
        }
    }

    @Override
    protected List<TripRecord> getItemsFromWrapper(@NotNull TripWrapper wrapper) {
        return wrapper.getTrips();
    }

    @Override
    protected TripWrapper createWrapper(List<TripRecord> items) {
        return new TripWrapper(items);
    }

    /**
     * Вспомогательный класс для обертки списка поездок в XML.
     */
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "trips")
    protected static class TripWrapper {
        private List<TripRecord> trips;
        @XmlElement(name = "trip")
        public List<TripRecord> getTrips() {
            return trips;
        }
    }
}

