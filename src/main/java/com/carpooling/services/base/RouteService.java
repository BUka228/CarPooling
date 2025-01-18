package com.carpooling.services.base;

import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.service.RouteServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с маршрутами.
 * Предоставляет методы для создания, получения, обновления и удаления маршрутов.
 */
public interface RouteService {

    /**
     * Создание нового маршрута.
     *
     * @param route Маршрут для создания.
     * @return ID созданного маршрута.
     * @throws RouteServiceException Если произошла ошибка при создании.
     */
    String createRoute(Route route) throws RouteServiceException;

    /**
     * Получение маршрута по ID.
     *
     * @param routeId ID маршрута.
     * @return Маршрут, если найден.
     * @throws RouteServiceException Если маршрут не найден или произошла ошибка.
     */
    Optional<Route> getRouteById(String routeId) throws RouteServiceException;

    /**
     * Получение всех маршрутов.
     *
     * @return Список всех маршрутов.
     * @throws RouteServiceException Если произошла ошибка при получении.
     */
    List<Route> getAllRoutes() throws RouteServiceException;

    /**
     * Обновление данных маршрута.
     *
     * @param route Маршрут с обновленными данными.
     * @throws RouteServiceException Если произошла ошибка при обновлении.
     */
    void updateRoute(Route route) throws RouteServiceException;

    /**
     * Удаление маршрута по ID.
     *
     * @param routeId ID маршрута.
     * @throws RouteServiceException Если произошла ошибка при удалении.
     */
    void deleteRoute(String routeId) throws RouteServiceException;

    /**
     * Поиск маршрутов по начальной точке.
     *
     * @param startPoint Начальная точка маршрута.
     * @return Список маршрутов, начинающихся с указанной точки.
     * @throws RouteServiceException Если произошла ошибка при поиске.
     */
    List<Route> findRoutesByStartPoint(String startPoint) throws RouteServiceException;

    /**
     * Поиск маршрутов по конечной точке.
     *
     * @param endPoint Конечная точка маршрута.
     * @return Список маршрутов, заканчивающихся в указанной точке.
     * @throws RouteServiceException Если произошла ошибка при поиске.
     */
    List<Route> findRoutesByEndPoint(String endPoint) throws RouteServiceException;

    /**
     * Поиск маршрутов по начальной и конечной точкам.
     *
     * @param startPoint Начальная точка маршрута.
     * @param endPoint   Конечная точка маршрута.
     * @return Список маршрутов, соответствующих указанным точкам.
     * @throws RouteServiceException Если произошла ошибка при поиске.
     */
    List<Route> findRoutesByStartAndEndPoints(String startPoint, String endPoint) throws RouteServiceException;

    /**
     * Получение маршрутов, созданных пользователем.
     *
     * @param userId ID пользователя.
     * @return Список маршрутов, созданных пользователем.
     * @throws RouteServiceException Если произошла ошибка при получении.
     */
    List<Route> getRoutesByUser(String userId) throws RouteServiceException;

    /**
     * Получение маршрутов с определенной датой.
     *
     * @param date Дата маршрута.
     * @return Список маршрутов с указанной датой.
     * @throws RouteServiceException Если произошла ошибка при получении.
     */
    List<Route> getRoutesByDate(String date) throws RouteServiceException;
}