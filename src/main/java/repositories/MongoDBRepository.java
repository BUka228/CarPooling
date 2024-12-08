package repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.JsonConversionUtils.jsonArrayToObjectList;
import static utils.JsonConversionUtils.objectListToMapList;

public class MongoDBRepository<T> implements GenericRepository<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> entityType;
    private final MongoCollection<Document> collection;

    public MongoDBRepository(Class<T> entityType, MongoCollection<Document> collection) {
        this.entityType = entityType;
        this.collection = collection;
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Сохранение всех объектов в MongoDB после их преобразования в Map.
     */
    public void saveAll(List<T> entities) {
        try {
            // Преобразуем список объектов в список Map<String, Object>
            List<Map<String, Object>> mapList = objectListToMapList(entities);

            // Преобразуем List<Map<String, Object>> в List<Document> для MongoDB
            List<Document> documents = objectListToDocuments(mapList);

            // Сохраняем все документы в коллекцию
            collection.insertMany(documents);
            //log.info("Успешно сохранено {} объектов в MongoDB", entities.size());
        } catch (Exception e) {
            log.error("Не удалось сохранить объекты в MongoDB", e);
            throw new RuntimeException("Ошибка при сохранении объектов", e);
        }
    }

    /**
     * Метод для сохранения объекта в MongoDB.
     */
    @Override
    public void save(T entity) {
        try {
            log.info(entity.toString());
            String json = objectMapper.writeValueAsString(entity);
            log.info("Сохраняем документ: {}", json);
            Document doc = Document.parse(json);
            log.info("Сохраняем документ: {}", doc.keySet());
            collection.insertOne(doc);
            //log.info("Успешно сохранён объект типа {}: {}", entityType.getSimpleName(), entity);
        } catch (Exception e) {
            log.error("Не удалось сохранить объект типа {}: {}", entityType.getSimpleName(), entity, e);
            throw new RuntimeException("Ошибка при сохранении объекта", e);
        }
    }

    /**
     * Метод для получения всех объектов из MongoDB.
     */
    @Override
    public List<T> findAll() {
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            // Собираем все документы из MongoDB
            List<Map<String, Object>> mapList = new ArrayList<>();
            while (cursor.hasNext()) {
                // Преобразуем Document в Map<String, Object>
                Map<String, Object> map = cursor.next();
                map.remove("_id"); //хз почему он добавляется
                mapList.add(map);
            }
            // Преобразуем List<Map<String, Object>> в List<T> с помощью универсальной функции
            log.info("Извлеченный документ: {}", mapList.toString());
            return jsonArrayToObjectList(mapList, entityType);
        } catch (Exception e) {
            log.error("Не удалось получить объекты типа {}", entityType.getSimpleName(), e);
            throw new RuntimeException("Ошибка при получении объектов", e);
        }
    }

    /**
     * Удаление всех объектов.
     */
    @Override
    public void deleteAll() {
        try {
            collection.deleteMany(new Document());
            //log.info("Успешно удалены все объекты типа {}", entityType.getSimpleName());
        } catch (Exception e) {
            log.error("Не удалось удалить объекты типа {}", entityType.getSimpleName(), e);
            throw new RuntimeException("Ошибка при удалении объектов", e);
        }
    }

    /**
     * Преобразование списка Map в список Document.
     */
    private List<Document> objectListToDocuments(List<Map<String, Object>> mapList) {
        List<Document> documents = new ArrayList<>();
        try {
            for (Map<String, Object> map : mapList) {
                String json = objectMapper.writeValueAsString(map);
                Document doc = Document.parse(json);
                documents.add(doc);
            }
            return documents;
        } catch (Exception e) {
            log.error("Не удалось преобразовать объект в Document", e);
            throw new RuntimeException("Ошибка при преобразовании объекта в Document", e);
        }
    }




    /*//Сохранение записи в базу данных
    public void save(HistoryContent historyContent) {
        try {
            // Сериализация объекта HistoryContent в JSON
            String json = objectMapper.writeValueAsString(historyContent);

            // Преобразование JSON в BSON-документ для MongoDB
            Document doc = Document.parse(json);

            // Сохранение документа в коллекцию
            collection.insertOne(doc);
            log.info("Successfully saved history content with ID: {}", historyContent.getId());
        } catch (Exception e) {
            log.error("Failed to save history content: {}", historyContent, e);
            throw new RuntimeException("Error saving history content", e);
        }
    }

    //Получение всех записей истории
    public List<HistoryContent> findAll() {
        List<HistoryContent> historyList = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                HistoryContent historyContent = objectMapper.readValue(doc.toJson(), HistoryContent.class);
                historyList.add(historyContent);
            }
            log.info("Successfully retrieved {} history entries", historyList.size());
        } catch (Exception e) {
            log.error("Failed to retrieve history entries", e);
            throw new RuntimeException("Error retrieving history entries", e);
        }
        return historyList;
    }

    //Удаление всех записей из коллекции
    public void deleteAll() {
        try {
            collection.deleteMany(new Document());
            log.info("Successfully deleted all history entries");
        } catch (Exception e) {
            log.error("Failed to delete history entries", e);
            throw new RuntimeException("Error deleting history entries", e);
        }
    }*/

}
