package providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MongoDBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MongoDataProvider<T> implements IDataProvider<T> {
    private final MongoCollection<Document> collection;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;


    public MongoDataProvider(MongoCollection<Document> collection, Class<T> type) {
        this.collection = collection;
        this.type = type;
    }

    @Override
    public void initDataSource(String filePath) {
        // Инициализация MongoDB не требует создания файлов или начальных данных.
        log.info("MongoDB collection initialized: {}", collection.getNamespace().getCollectionName());
    }

    @Override
    public void saveRecord(T record) {
        try {
            String id = extractId(record); // Получаем значение id
            Document document = Document.parse(objectMapper.writeValueAsString(record));
            // Устанавливаем значение _id
            document.put("_id", id);
            document.remove("id"); // Удаляем поле "id", чтобы не дублировать
            // Сохраняем документ в коллекцию с upsert
            collection.replaceOne(Filters.eq("_id", id), document, new ReplaceOptions().upsert(true));
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи: {}", record, e);
            throw new DataProviderException("Не удалось сохранить запись", e);
        }
    }

    @Override
    public void deleteRecord(long id) {
        try {
            collection.deleteOne(Filters.eq("_id", String.valueOf(id)));
        } catch (Exception e) {
            log.error("Ошибка при удалении записи с ID: {}", id, e);
            throw new DataProviderException("Не удалось удалить запись", e);
        }
    }

    @Override
    public T getRecordById(long id) {
        try {
            Document document = collection.find(Filters.eq("_id", String.valueOf(id))).first();
            if (document != null) {
                nameChangeId(document);
                return objectMapper.readValue(document.toJson(), type);
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Ошибка при получении записи с ID: {}", id, e);
            throw new DataProviderException("Не удалось получить запись", e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        try {
            List<T> records = new ArrayList<>();
            for (Document document : collection.find()) {
                Object idValue = document.get("_id");
                nameChangeId(document);
                records.add(objectMapper.readValue(document.toJson(), type));
            }
            return records;
        } catch (Exception e) {
            log.error("Ошибка при чтении всех записей", e);
            throw new DataProviderException("Не удалось прочитать все записи", e);
        }
    }

    private String extractId(T record) {
        try {
            return objectMapper.convertValue(record, Document.class).get("id").toString();
        } catch (Exception e) {
            throw new DataProviderException("Не удалось извлечь ID из записи", e);
        }
    }

    private  void nameChangeId(Document document) {
        Object idValue = document.get("_id");
        if (idValue != null) {
            document.put("id", idValue);
            document.remove("_id");
        }
    }
}