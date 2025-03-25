package com.carpooling.dao.base;

import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * DAO интерфейс для работы с маршрутами.
 */
public interface RouteDao {
    /**
     * Создает новый маршрут в базе данных.
     *
     * @param route объект {@link com.carpooling.entities.database.Route}, содержащий информацию о маршруте.
     * @return {@link Optional} с ID созданного маршрута, или {@link Optional#empty()} в случае неудачи.
     * @throws DataAccessException если произошла ошибка при создании маршрута.
     */
    String createRoute(Route route) throws DataAccessException;

    /**
     * Получает маршрут из базы данных по его ID.
     *
     * @param id уникальный идентификатор маршрута.
     * @return {@link Optional} с объектом {@link com.carpooling.entities.database.Route}, или {@link Optional#empty()} если маршрут не найден.
     * @throws DataAccessException если маршрут не найден.
     */
    Optional<Route> getRouteById(String id) throws DataAccessException;

    /**
     * Обновляет информацию о маршруте в базе данных.
     *
     * @param route объект {@link com.carpooling.entities.database.Route} с обновленной информацией.
     * @throws DataAccessException если произошла ошибка при обновлении маршрута.
     */
    void updateRoute(Route route) throws DataAccessException;

    /**
     * Удаляет маршрут из базы данных по его ID.
     *
     * @param id уникальный идентификатор маршрута.
     * @throws DataAccessException если маршрут не найден.
     */
    void deleteRoute(String id) throws DataAccessException;
}

