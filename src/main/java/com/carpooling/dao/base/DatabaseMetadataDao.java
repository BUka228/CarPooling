package com.carpooling.dao.base;

import com.carpooling.exceptions.dao.DataAccessException;
import java.util.List;
import java.util.Map;

public interface DatabaseMetadataDao {

    /**
     * Получает имя и версию используемой СУБД.
     * @return Строка с именем и версией СУБД.
     * @throws DataAccessException если произошла ошибка доступа к данным.
     */
    String getDatabaseProductNameAndVersion() throws DataAccessException;

    /**
     * Получает список имен таблиц в текущей схеме.
     * @return Список имен таблиц.
     * @throws DataAccessException если произошла ошибка доступа к данным.
     */
    List<String> getTableNames() throws DataAccessException;

    /**
     * Получает количество строк в указанной таблице.
     * @param tableName имя таблицы.
     * @return Количество строк.
     * @throws DataAccessException если таблица не найдена или произошла ошибка.
     */
    long getTableRowCount(String tableName) throws DataAccessException;

    /**
     * Получает информацию о колонках указанной таблицы.
     * @param tableName имя таблицы.
     * @return Map, где ключ - имя колонки, значение - тип данных колонки.
     * @throws DataAccessException если таблица не найдена или произошла ошибка.
     */
    Map<String, String> getTableColumnInfo(String tableName) throws DataAccessException;

    /**
     * Получает размер текущей базы данных (специфично для СУБД, пример для PostgreSQL).
     * @return Размер базы данных в читаемом формате (например, "10 MB").
     * @throws DataAccessException если произошла ошибка.
     */
    String getDatabaseSize() throws DataAccessException;
}