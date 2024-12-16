package providers;

import com.mongodb.client.MongoCollection;
import converters.GenericConverter;
import exceptions.DataProviderException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoDataProvider<T> implements IDataProvider<T> {
    private final MongoCollection<Document> collection;
    private final GenericConverter<T, Document> converter;
    private static final Logger log = LoggerFactory.getLogger(MongoDataProvider.class);

    public MongoDataProvider(MongoCollection<Document> collection, GenericConverter<T, Document> converter) {
        this.collection = collection;
        this.converter = converter;
    }

    @Override
    public void saveRecord(T record) {
        try {
            Document doc = converter.serialize(record);
            collection.insertOne(doc);
            log.info("Запись с ID {} успешно сохранена в MongoDB", converter.getId(record));
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи в MongoDB: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при сохранении записи в MongoDB", e);
        }
    }

    @Override
    public void deleteRecord(T record) {
        try {
            Document query = new Document("id", converter.getId(record));
            collection.deleteOne(query);
            log.info("Запись с ID {} успешно удалена из MongoDB", converter.getId(record));
        } catch (Exception e) {
            log.error("Ошибка при удалении записи из MongoDB: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при удалении записи из MongoDB", e);
        }
    }

    @Override
    public T getRecordById(String id) {
        try {
            Document query = new Document("id", id);
            Document doc = collection.find(query).first();

            if (doc != null) {
                log.info("Запись с ID {} найдена в MongoDB", id);
                doc.remove("_id");
                return converter.deserialize(doc);
            } else {
                log.warn("Запись с ID {} не найдена в MongoDB", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при получении записи из MongoDB по ID {}: {}", id, e.getMessage(), e);
            throw new DataProviderException("Ошибка при получении записи из MongoDB по ID", e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        try {
            List<T> result = new ArrayList<>();
            for (Document doc : collection.find()) {
                doc.remove("_id");
                result.add(converter.deserialize(doc));
            }
            log.info("Получено {} записей из MongoDB", result.size());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при получении всех записей из MongoDB: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при получении всех записей из MongoDB", e);
        }
    }

    @Override
    public void initDataSource() {
        // MongoDB не требует явной инициализации
        log.info("Инициализация источника данных MongoDB не требуется.");
    }
}
