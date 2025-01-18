package com.carpooling.services.base;

import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.TripServiceException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с поездками.
 * Предоставляет методы для создания, получения, обновления и удаления поездок.
 */
public interface TripService {

    /**
     * Создание новой поездки.
     *
     * @param trip    Поездка для создания.
     * @param route   Маршрут для создания.
     * @param userId  ID пользователя, создающего поездку.
     * @return ID созданной поездки.
     * @throws TripServiceException Если произошла ошибка при создании.
     */
    String createTrip(Trip trip, Route route, String userId) throws TripServiceException;

    /**
     * Получение поездки по ID.
     *
     * @param tripId ID поездки.
     * @return Поездка, если найдена.
     * @throws TripServiceException Если поездка не найдена или произошла ошибка.
     */
    Optional<Trip> getTripById(String tripId) throws TripServiceException;

    /**
     * Получение всех поездок.
     *
     * @return Список всех поездок.
     * @throws TripServiceException Если произошла ошибка при получении.
     */
    List<Trip> getAllTrips() throws TripServiceException;

    /**
     * Обновление данных поездки.
     *
     * @param trip    Поездка с обновленными данными.
     * @param route   Маршрут с обновленными данными.
     * @param userId  ID пользователя.
     * @throws TripServiceException Если произошла ошибка при обновлении.
     */
    void updateTrip(Trip trip, Route route, String userId) throws TripServiceException;

    /**
     * Удаление поездки по ID.
     *
     * @param tripId ID поездки.
     * @throws TripServiceException Если произошла ошибка при удалении.
     */
    void deleteTrip(String tripId) throws TripServiceException;

    /**
     * Получение поездок по ID пользователя.
     *
     * @param userId ID пользователя.
     * @return Список поездок пользователя.
     * @throws TripServiceException Если произошла ошибка при получении.
     */
    List<Trip> getTripsByUser(String userId) throws TripServiceException;

    /**
     * Получение поездок по статусу.
     *
     * @param status Статус поездки.
     * @return Список поездок с указанным статусом.
     * @throws TripServiceException Если произошла ошибка при получении.
     */
    List<Trip> getTripsByStatus(String status) throws TripServiceException;

    /**
     * Получение поездок по дате создания.
     *
     * @param date Дата создания поездки.
     * @return Список поездок с указанной датой создания.
     * @throws TripServiceException Если произошла ошибка при получении.
     */
    List<Trip> getTripsByCreationDate(String date) throws TripServiceException;

    /**
     * Получение поездок по маршруту.
     *
     * @param routeId ID маршрута.
     * @return Список поездок с указанным маршрутом.
     * @throws TripServiceException Если произошла ошибка при получении.
     */
    List<Trip> getTripsByRoute(String routeId) throws TripServiceException;
}