package com.carpooling.transaction;

import com.carpooling.exceptions.dao.DataAccessException;

public interface DataAccessManager {
    /**
     * Выполняет действие в рамках транзакции (если применимо).
     * @param action Действие для выполнения.
     * @param <R> Тип результата.
     * @return Результат выполнения действия.
     * @throws DataAccessException Если произошла ошибка при выполнении или управлении транзакцией.
     */
    <R> R executeInTransaction(DataAccessAction<R> action) throws DataAccessException;

    /**
     * Выполняет действие только для чтения (без явной транзакции, но с управлением сессией, если нужно).
     * @param action Действие для выполнения.
     * @param <R> Тип результата.
     * @return Результат выполнения действия.
     * @throws DataAccessException Если произошла ошибка при выполнении или управлении сессией.
     */
    <R> R executeReadOnly(DataAccessAction<R> action) throws DataAccessException;
}