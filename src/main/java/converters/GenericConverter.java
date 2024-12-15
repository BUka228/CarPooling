package converters;

public interface GenericConverter<T, R> {
    R serialize(T entity) throws Exception;       // Сериализация объекта в нужный формат (например, Document, String)
    T deserialize(R data) throws Exception;      // Десериализация данных в объект
    String getId(T entity);                      // Получение ID объекта
}