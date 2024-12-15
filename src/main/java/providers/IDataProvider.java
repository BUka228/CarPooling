package providers;

import exceptions.DataProviderException;

import java.util.List;

public interface IDataProvider<T> {
    void saveRecord(T record) throws DataProviderException;       // Сохранить объект
    void deleteRecord(T record) throws DataProviderException;    // Удалить объект
    T getRecordById(String id) throws DataProviderException;     // Получить объект по ID
    List<T> getAllRecords() throws DataProviderException;        // Получить все объекты
    void initDataSource() throws DataProviderException;          // Инициализировать источник данных
}

