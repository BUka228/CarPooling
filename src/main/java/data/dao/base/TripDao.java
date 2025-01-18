package data.dao.base;

import data.model.record.TripRecord;
import exceptions.dao.DataAccessException;

import java.util.Optional;


/**
 * DAO интерфейс для работы с поездками.
 */
public interface TripDao {
    /**
     * Создает новую поездку.
     *
     * @param tripRecord  Информация о поездке.
     * @return ID созданной поездки.
     * @throws DataAccessException Если произошла ошибка при создании поездки.
     */
    String createTrip(TripRecord tripRecord) throws DataAccessException;

    /**
     * Возвращает поездку по её ID.
     *
     * @param id ID поездки.
     * @return Поездка, если найдена, Optional.empty() иначе.
     * @throws DataAccessException Если поездка не найдена.
     */
    Optional<TripRecord> getTripById(String id) throws DataAccessException;

    /**
     * Обновляет информацию о поездке.
     *
     * @param tripRecord    Информация о поездке.
     * @throws DataAccessException Если произошла ошибка при обновлении поездки.
     */
    void updateTrip(TripRecord tripRecord) throws DataAccessException;

    /**
     * Удаляет поездку по её ID.
     *
     * @param id ID поездки.
     * @throws DataAccessException Если произошла ошибка при удалении поездки.
     */
    void deleteTrip(String id) throws DataAccessException;
}