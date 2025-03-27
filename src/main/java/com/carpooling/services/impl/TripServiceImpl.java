package com.carpooling.services.impl;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.entities.enums.TripStatus;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.TripException;
import com.carpooling.services.base.TripService;
import com.carpooling.transaction.DataAccessManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class TripServiceImpl implements TripService {

    private final TripDao tripDao;
    private final RouteDao routeDao;
    private final UserDao userDao;
    private final DataAccessManager dataAccessManager;

    public TripServiceImpl(TripDao tripDao, RouteDao routeDao, UserDao userDao, DataAccessManager dataAccessManager) {
        this.tripDao = tripDao;
        this.routeDao = routeDao;
        this.userDao = userDao;
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public String createTrip(String userId, String startPoint, String endPoint,
                             LocalDateTime departureDateTime, byte maxPassengers)
            throws TripException, DataAccessException {
        log.debug("Attempting to create trip for user ID: {}", userId);

        // Валидация входных данных
        if (startPoint == null || startPoint.isBlank() || endPoint == null || endPoint.isBlank()) {
            throw new TripException("Начальная и конечная точки маршрута не могут быть пустыми.");
        }
        if (departureDateTime == null || departureDateTime.isBefore(LocalDateTime.now())) {
            throw new TripException("Время отправления должно быть в будущем.");
        }
        if (maxPassengers <= 0) {
            throw new TripException("Количество пассажиров должно быть положительным.");
        }

        // Выполняем всю логику создания в одной транзакции
        return dataAccessManager.executeInTransaction(() -> {
            // 1. Получаем пользователя
            Optional<User> userOpt = userDao.getUserById(userId); // Чтение
            User creator = userOpt.orElseThrow(() -> new TripException("Создатель поездки с ID " + userId + " не найден."));
            log.trace("Trip creator found: {}", creator.getEmail());

            // 2. Создаем маршрут
            Route route = new Route();
            route.setStartingPoint(startPoint);
            route.setEndingPoint(endPoint);
            log.debug("Attempting to persist route from {} to {}", startPoint, endPoint);
            String routeId = routeDao.createRoute(route); // Запись
            try {
                route.setId(UUID.fromString(routeId)); // Устанавливаем ID для связи
            } catch (IllegalArgumentException e) {
                throw new TripException("Invalid route ID generated: " + routeId, e);
            }
            log.info("Route persisted with ID: {}", routeId);

            // 3. Создаем поездку
            Trip trip = new Trip();
            trip.setUser(creator);
            trip.setRoute(route);
            trip.setDepartureTime(departureDateTime);
            trip.setMaxPassengers(maxPassengers);
            trip.setStatus(TripStatus.PLANNED);
            trip.setEditable(true);

            // 4. Сохраняем поездку
            log.debug("Attempting to persist trip with route ID: {}...", route.getId());
            String tripId = tripDao.createTrip(trip); // Запись
            log.info("Trip persisted successfully with ID: {}", tripId);
            return tripId;
        });
        // Ошибки DataAccessException будут обработаны в dataAccessManager
    }

    @Override
    public Optional<Trip> getTripById(String tripId) throws DataAccessException {
        log.debug("Fetching trip by ID: {}", tripId);
        // Чтение
        return dataAccessManager.executeReadOnly(() ->
                tripDao.getTripById(tripId)
        );
    }

    @Override
    public List<Trip> findTrips(String startPoint, String endPoint, LocalDate date)
            throws OperationNotSupportedException, DataAccessException {
        log.debug("Finding trips with criteria: start={}, end={}, date={}", startPoint, endPoint, date);
        // Чтение
        return dataAccessManager.executeReadOnly(() ->
                tripDao.findTrips(startPoint, endPoint, date)
        );
    }

    @Override
    public void cancelTrip(String tripId, String userId)
            throws TripException, OperationNotSupportedException, DataAccessException {
        log.debug("Attempting to cancel trip ID: {} by user ID: {}", tripId, userId);

        dataAccessManager.executeInTransaction(() -> {
            Optional<Trip> tripOpt = tripDao.getTripById(tripId); // Чтение
            Trip trip = tripOpt.orElseThrow(() -> new TripException("Поездка с ID " + tripId + " не найдена."));

            // 1. Проверка прав
            if (trip.getUser() == null || !trip.getUser().getId().toString().equals(userId)) { // Проверка на null
                throw new TripException("У вас нет прав для отмены этой поездки.");
            }

            // 2. Проверка статуса
            if (trip.getStatus() == TripStatus.CANCELLED || trip.getStatus() == TripStatus.COMPLETED) {
                log.info("Trip {} is already {} - cancellation skipped.", tripId, trip.getStatus());
                return null;
            }

            // 3. Обновление статуса
            trip.setStatus(TripStatus.CANCELLED);
            log.trace("Setting trip {} status to CANCELLED", tripId);

            // 4. Сохранение изменений
            tripDao.updateTrip(trip); // Запись
            log.info("Trip {} cancelled successfully by user {}", tripId, userId);
            // TODO: Уведомление пассажиров (может быть отдельным процессом/событием)
            return null;
        });
    }
}