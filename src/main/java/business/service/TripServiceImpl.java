package business.service;

import business.base.RouteService;
import business.base.TripService;
import com.man.constant.ErrorMessages;
import com.man.constant.LogMessages;
import data.dao.base.TripDao;
import data.model.database.Route;
import data.model.database.Trip;
import data.model.record.TripRecord;
import exceptions.service.TripServiceException;
import factory.DaoFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import presentation.context.CliContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса TripService.
 * Предоставляет методы для работы с поездками, включая создание, получение, обновление и удаление.
 */
@Slf4j
@AllArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripDao tripDao;
    private final RouteService routeService;

    public TripServiceImpl() {
        this.tripDao = DaoFactory.getTripDao(CliContext.getCurrentStorageType());
        this.routeService = new RouteServiceImpl();
    }

    @Override
    public String createTrip(@NotNull Trip trip, @NotNull Route route, String userId) throws TripServiceException {
        log.info(LogMessages.TRIP_CREATION_START, trip.getDepartureTime(), route.getStartPoint(), route.getEndPoint());
        try {
            // Создаем маршрут
            String routeId = routeService.createRoute(route);
            log.info("Маршрут успешно создан: {}", routeId);

            // Создаем поездку
            TripRecord tripRecord = new TripRecord(trip, userId, routeId);
            String tripId = tripDao.createTrip(tripRecord);
            log.info(LogMessages.TRIP_CREATION_SUCCESS, tripId);
            return tripId;
        } catch (Exception e) {
            log.error(LogMessages.TRIP_CREATION_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<Trip> getTripById(String tripId) throws TripServiceException {
        log.info(LogMessages.TRIP_SEARCH_BY_ID_START, tripId);
        try {
            Optional<TripRecord> tripOptional = tripDao.getTripById(tripId);
            if (tripOptional.isEmpty()) {
                log.warn(LogMessages.TRIP_SEARCH_BY_ID_ERROR, tripId);
            }
            log.info(LogMessages.TRIP_SEARCH_BY_ID_SUCCESS, tripId);
            return tripOptional.map(TripRecord::toTrip);
        } catch (Exception e) {
            log.error(LogMessages.TRIP_SEARCH_BY_ID_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Trip> getAllTrips() throws TripServiceException {
        log.info(LogMessages.TRIP_GET_ALL_START);
        try {
            List<Trip> trips = Collections.emptyList();
            log.info(LogMessages.TRIP_GET_ALL_SUCCESS);
            return trips;
        } catch (Exception e) {
            log.error(LogMessages.TRIP_GET_ALL_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_GET_ALL_ERROR, e);
        }
    }

    @Override
    public void updateTrip(@NotNull Trip trip, Route route, String userId) throws TripServiceException {
        log.info(LogMessages.TRIP_UPDATE_START, trip.getId());
        try {
            // Обновляем маршрут
            Optional<TripRecord> tripRecordOld = tripDao.getTripById(trip.getId());
            route.setId(tripRecordOld.get().getRouteId());
            routeService.updateRoute(route);
            log.info("Маршрут успешно обновлен: {}", route.getId());

            // Обновляем поездку
            TripRecord tripRecordNew = new TripRecord(trip, userId, route.getId());
            tripDao.updateTrip(tripRecordNew);
            log.info(LogMessages.TRIP_UPDATE_SUCCESS, trip.getId());
        } catch (Exception e) {
            log.error(LogMessages.TRIP_UPDATE_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteTrip(String tripId) throws TripServiceException {
        log.info(LogMessages.TRIP_DELETION_START, tripId);
        try {
            tripDao.deleteTrip(tripId);
            log.info(LogMessages.TRIP_DELETION_SUCCESS, tripId);
        } catch (Exception e) {
            log.error(LogMessages.TRIP_DELETION_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_DELETION_ERROR, e);
        }
    }

    @Override
    public List<Trip> getTripsByUser(String userId) throws TripServiceException {
        log.info(LogMessages.TRIP_GET_BY_USER_START, userId);
        try {
            throw new UnsupportedOperationException("Метод getTripsByUser не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.TRIP_GET_BY_USER_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_GET_BY_USER_ERROR, e);
        }
    }

    @Override
    public List<Trip> getTripsByStatus(String status) throws TripServiceException {
        log.info(LogMessages.TRIP_GET_BY_STATUS_START, status);
        try {
            throw new UnsupportedOperationException("Метод getTripsByStatus не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.TRIP_GET_BY_STATUS_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_GET_BY_STATUS_ERROR, e);
        }
    }

    @Override
    public List<Trip> getTripsByCreationDate(String date) throws TripServiceException {
        log.info(LogMessages.TRIP_GET_BY_CREATION_DATE_START, date);
        try {
            throw new UnsupportedOperationException("Метод getTripsByCreationDate не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.TRIP_GET_BY_CREATION_DATE_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_GET_BY_CREATION_DATE_ERROR, e);
        }
    }

    @Override
    public List<Trip> getTripsByRoute(String routeId) throws TripServiceException {
        log.info(LogMessages.TRIP_GET_BY_ROUTE_START, routeId);
        try {
            throw new UnsupportedOperationException("Метод getTripsByRoute не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.TRIP_GET_BY_ROUTE_ERROR, e.getMessage());
            throw new TripServiceException(ErrorMessages.TRIP_GET_BY_ROUTE_ERROR, e);
        }
    }
}