package com.carpooling.dao.base;

import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;


/**
 * DAO интерфейс для работы с поездками.
 */
public interface TripDao {
    /**
     * Создает новую поездку.
     *
     * @param trip  Информация о поездке.
     * @return ID созданной поездки.
     * @throws DataAccessException Если произошла ошибка при создании поездки.
     */
    String createTrip(Trip trip) throws DataAccessException;

    /**
     * Возвращает поездку по её ID.
     *
     * @param id ID поездки.
     * @return Поездка, если найдена, Optional.empty() иначе.
     * @throws DataAccessException Если поездка не найдена.
     */
    Optional<Trip> getTripById(String id) throws DataAccessException;

    /**
     * Обновляет информацию о поездке.
     *
     * @param trip    Информация о поездке.
     * @throws DataAccessException Если произошла ошибка при обновлении поездки.
     */
    void updateTrip(Trip trip) throws DataAccessException;

    /**
     * Удаляет поездку по её ID.
     *
     * @param id ID поездки.
     * @throws DataAccessException Если произошла ошибка при удалении поездки.
     */
    void deleteTrip(String id) throws DataAccessException;
}