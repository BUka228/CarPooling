package com.carpooling.services.base;

import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.RouteException;

import java.util.Optional;

/**
 * Сервис для управления маршрутами (если они управляются отдельно от поездок).
 */
public interface RouteService {

    /**
     * Создает маршрут. Используется внутри TripService, если маршруты уникальны.
     * @param route Данные маршрута.
     * @return ID созданного маршрута.
     * @throws RouteException Если произошла ошибка.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    String createRoute(Route route) throws RouteException, DataAccessException;

    /**
     * Получает маршрут по ID.
     * @param routeId ID маршрута.
     * @return Optional с маршрутом.
     * @throws DataAccessException Если произошла ошибка доступа к данным.
     */
    Optional<Route> getRouteById(String routeId) throws DataAccessException;

}
