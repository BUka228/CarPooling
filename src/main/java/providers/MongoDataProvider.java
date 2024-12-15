package providers;

import com.mongodb.client.MongoCollection;
import converters.GenericConverter;
import exceptions.DataProviderException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDataProvider<T> implements IDataProvider<T> {
    private final MongoCollection<Document> collection;
    private final GenericConverter<T, Document> converter;




    public MongoDataProvider(MongoCollection<Document> collection, GenericConverter<T, Document> converter) {
        this.collection = collection;
        this.converter = converter;
    }

    @Override
    public void saveRecord(T record) {
        try {
            Document doc = converter.serialize(record);
            collection.insertOne(doc);
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при сохранении записи в MongoDB", e);
        }
    }

    @Override
    public void deleteRecord(T record) {
        try {
            Document query = new Document("_id", converter.getId(record));
            collection.deleteOne(query);
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при удалении записи из MongoDB", e);
        }
    }

    @Override
    public T getRecordById(String id) {
        try {
            Document query = new Document("_id", id);
            Document doc = collection.find(query).first();
            return doc != null ? converter.deserialize(doc) : null;
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при получении записи из MongoDB по ID", e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        try {
            List<T> result = new ArrayList<>();
            for (Document doc : collection.find()) {
                result.add(converter.deserialize(doc));
            }
            return result;
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при получении всех записей из MongoDB", e);
        }
    }

    @Override
    public void initDataSource() {
        // MongoDB не требует явной инициализации
    }
}
