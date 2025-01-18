package business.service;

import business.base.RouteService;
import com.man.constant.ErrorMessages;
import com.man.constant.LogMessages;
import data.dao.base.RouteDao;
import data.model.database.Route;
import data.model.record.RouteRecord;
import exceptions.service.RouteServiceException;
import factory.DaoFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import presentation.context.CliContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;



/**
 * Реализация интерфейса RouteService.
 * Предоставляет методы для работы с маршрутами, включая создание, получение, обновление и удаление.
 */
@Slf4j
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {
    
    private final RouteDao routeDao;

    public RouteServiceImpl() {
        this.routeDao = DaoFactory.getRouteDao(CliContext.getCurrentStorageType());
    }

    @Override
    public String createRoute(@NotNull Route route) throws RouteServiceException {
        log.info(LogMessages.ROUTE_CREATION_START, route.getStartPoint(), route.getEndPoint());
        try {
            RouteRecord routeRecord = new RouteRecord(route);
            String routeId = routeDao.createRoute(routeRecord);
            log.info(LogMessages.ROUTE_CREATION_SUCCESS, routeId);
            return routeId;
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_CREATION_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<Route> getRouteById(String routeId) throws RouteServiceException {
        log.info(LogMessages.ROUTE_SEARCH_BY_ID_START, routeId);
        try {
            Optional<RouteRecord> routeOptional = routeDao.getRouteById(routeId);
            if (routeOptional.isEmpty()) {
                log.warn(LogMessages.ROUTE_SEARCH_BY_ID_ERROR, routeId);
            }
            log.info(LogMessages.ROUTE_SEARCH_BY_ID_SUCCESS, routeId);
            return routeOptional.map(RouteRecord::toRoute);
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_SEARCH_BY_ID_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Route> getAllRoutes() throws RouteServiceException {
        log.info(LogMessages.ROUTE_GET_ALL_START);
        try {
            List<Route> routes = Collections.emptyList();
            log.info(LogMessages.ROUTE_GET_ALL_SUCCESS);
            return routes;
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_GET_ALL_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_GET_ALL_ERROR, e);
        }
    }

    @Override
    public void updateRoute(@NotNull Route route) throws RouteServiceException {
        log.info(LogMessages.ROUTE_UPDATE_START, route.getId());
        try {
            RouteRecord routeRecord = new RouteRecord(route);
            routeDao.updateRoute(routeRecord);
            log.info(LogMessages.ROUTE_UPDATE_SUCCESS, route.getId());
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_UPDATE_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRoute(String routeId) throws RouteServiceException {
        log.info(LogMessages.ROUTE_DELETION_START, routeId);
        try {
            routeDao.deleteRoute(routeId);
            log.info(LogMessages.ROUTE_DELETION_SUCCESS, routeId);
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_DELETION_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_DELETION_ERROR, e);
        }
    }

    @Override
    public List<Route> findRoutesByStartPoint(String startPoint) throws RouteServiceException {
        log.info(LogMessages.ROUTE_SEARCH_BY_START_POINT_START, startPoint);
        try {
            throw new UnsupportedOperationException("Метод findRoutesByStartPoint не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_SEARCH_BY_START_POINT_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Route> findRoutesByEndPoint(String endPoint) throws RouteServiceException {
        log.info(LogMessages.ROUTE_SEARCH_BY_END_POINT_START, endPoint);
        try {
            throw new UnsupportedOperationException("Метод findRoutesByEndPoint не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_SEARCH_BY_END_POINT_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Route> findRoutesByStartAndEndPoints(String startPoint, String endPoint) throws RouteServiceException {
        log.info(LogMessages.ROUTE_SEARCH_BY_START_AND_END_POINTS_START, startPoint, endPoint);
        try {
            throw new UnsupportedOperationException("Метод findRoutesByStartAndEndPoints не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_SEARCH_BY_START_AND_END_POINTS_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_SEARCH_ERROR, e);
        }
    }

    @Override
    public List<Route> getRoutesByUser(String userId) throws RouteServiceException {
        log.info(LogMessages.ROUTE_GET_BY_USER_START, userId);
        try {
            throw new UnsupportedOperationException("Метод getRoutesByUser не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_GET_BY_USER_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_GET_BY_USER_ERROR, e);
        }
    }

    @Override
    public List<Route> getRoutesByDate(String date) throws RouteServiceException {
        log.info(LogMessages.ROUTE_GET_BY_DATE_START, date);
        try {
            throw new UnsupportedOperationException("Метод getRoutesByDate не реализован.");
        } catch (Exception e) {
            log.error(LogMessages.ROUTE_GET_BY_DATE_ERROR, e.getMessage());
            throw new RouteServiceException(ErrorMessages.ROUTE_GET_BY_DATE_ERROR, e);
        }
    }
}