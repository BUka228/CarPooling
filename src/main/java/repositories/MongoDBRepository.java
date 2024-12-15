package repositories;

import exceptions.RepositoryException;
import providers.IDataProvider;

import java.util.List;

public class MongoDBRepository<T> extends GenericRepository<T> {
    public MongoDBRepository(IDataProvider<T> provider) {
        super(provider);
    }

    public List<T> findByField(String fieldName, String value) {
        try {
            return findAll().stream()
                    .filter(record -> value.equals(getFieldValue(record, fieldName)))
                    .toList();
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при фильтрации по полю " + fieldName, e);
        }
    }

    private Object getFieldValue(T record, String fieldName) {
        try {
            var field = record.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(record);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при получении значения поля " + fieldName, e);
        }
    }



























    /*protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> entityType;
    private final MongoCollection<Document> collection;

    public MongoDBRepository(Class<T> entityType, MongoCollection<Document> collection) {
        this.entityType = entityType;
        this.collection = collection;
        objectMapper.registerModule(new JavaTimeModule());
    }

    *//**
     * Сохранение всех объектов в MongoDB после их преобразования в Map.
     *//*
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

    *//**
     * Метод для сохранения объекта в MongoDB.
     *//*
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

    *//**
     * Метод для получения всех объектов из MongoDB.
     *//*
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

    *//**
     * Удаление всех объектов.
     *//*
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

    *//**
     * Преобразование списка Map в список Document.
     *//*
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
    }*/
}
