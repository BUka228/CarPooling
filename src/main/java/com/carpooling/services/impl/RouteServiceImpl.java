package com.carpooling.services.impl;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.RouteException;
import com.carpooling.services.base.RouteService;
import com.carpooling.transaction.DataAccessManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteDao routeDao;
    private final DataAccessManager dataAccessManager;

    public RouteServiceImpl(RouteDao routeDao, DataAccessManager dataAccessManager) {
        this.routeDao = routeDao;
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public String createRoute(Route route) throws RouteException, DataAccessException {
        log.debug("Creating route from {} to {}", route.getStartingPoint(), route.getEndingPoint());
        if (route.getStartingPoint() == null || route.getStartingPoint().isBlank() ||
                route.getEndingPoint() == null || route.getEndingPoint().isBlank()) {
            throw new RouteException("Начальная и конечная точки маршрута не могут быть пустыми.");
        }

        return dataAccessManager.executeInTransaction(() -> {
            // Опционально: Поиск существующего маршрута перед созданием
            // Optional<Route> existing = findExistingRoute(route.getStartingPoint(), route.getEndingPoint());
            // if(existing.isPresent()) return existing.get().getId().toString();

            String routeId = routeDao.createRoute(route);
            log.info("Route created successfully with ID: {}", routeId);
            try {
                if (route.getId() == null && routeId != null) {
                    route.setId(UUID.fromString(routeId));
                }
            } catch (IllegalArgumentException e) { /* Логирование ошибки */ }
            return routeId;
        });
    }

    @Override
    public Optional<Route> getRouteById(String routeId) throws DataAccessException {
        log.debug("Fetching route by ID: {}", routeId);
        return dataAccessManager.executeReadOnly(() ->
                routeDao.getRouteById(routeId)
        );
    }

    // Пример приватного метода для поиска существующего маршрута (если нужно)
    // private Optional<Route> findExistingRoute(String start, String end) throws DataAccessException {
    //    // Этот метод тоже должен вызываться внутри executeReadOnly или executeInTransaction
    //    // Реализация поиска зависит от возможностей RouteDao
    //    log.debug("Checking for existing route: {} -> {}", start, end);
    //    // return routeDao.findByPoints(start, end); // Если такой метод есть
    //    return Optional.empty(); // Заглушка
    // }
}