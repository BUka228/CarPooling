package com.carpooling.dao.base;

import com.carpooling.entities.history.HistoryContent;
import com.carpooling.exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * DAO интерфейс для работы с историей контента.
 */
public interface HistoryContentDao {
    /**
     * Создает запись в истории контента.
     * @param historyContent Объект, представляющий запись истории.
     * @return Идентификатор созданной записи.
     * @throws DataAccessException Если произошла ошибка при создании записи.
     */
    String createHistory(HistoryContent historyContent) throws DataAccessException;
    /**
     * Возвращает запись истории по её идентификатору.
     * @param id Идентификатор записи истории.
     * @return Optional, содержащий запись истории, если она найдена, или пустой Optional, если запись не найдена.
     * @throws DataAccessException Если произошла ошибка при получении записи.
     */
    Optional<HistoryContent> getHistoryById(String id) throws DataAccessException;
    /**
     * Обновляет запись в истории контента.
     * @param historyContent Объект, представляющий запись истории с обновленными данными.
     * @throws DataAccessException Если произошла ошибка при обновлении записи.
     */
    void updateHistory(HistoryContent historyContent) throws DataAccessException;
    /**
     * Удаляет запись из истории контента по её идентификатору.
     * @param id Идентификатор записи истории.
     * @throws DataAccessException Если произошла ошибка при удалении записи.
     */
    void deleteHistory(String id) throws DataAccessException;
}
