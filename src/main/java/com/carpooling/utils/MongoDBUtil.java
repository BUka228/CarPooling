package com.carpooling.utils;

import com.carpooling.constants.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.io.IOException;

import static com.carpooling.constants.Constants.MONGO_DB;


@Slf4j
public class MongoDBUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MongoClient mongoClient;

    // Конструктор для инициализации клиента
    public MongoDBUtil() {
        try {
            String mongoUri = ConfigurationUtil.getConfigurationEntry(Constants.MONGO_URI);
            mongoClient = MongoClients.create(mongoUri);
        } catch (IOException e) {
            log.error("Ошибка при загрузке конфигурации MongoDB: {}", e.getMessage());
            throw new RuntimeException("Ошибка при загрузке конфигурации MongoDB", e);
        } catch (Exception e) {
            log.error("Ошибка при подключении к MongoDB: {}", e.getMessage());
            throw new RuntimeException("Ошибка при подключении к MongoDB", e);
        }
    }

    /**
     * Получение MongoDatabase по названию базы данных.
     */
    public MongoDatabase getDatabase(String dbName) {
        try {
            return mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            log.error("Ошибка при получении базы данных MongoDB: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении базы данных MongoDB", e);
        }
    }

    /**
     * Получение коллекции MongoCollection по имени базы данных и коллекции.
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        try {
            MongoDatabase database = getDatabase(ConfigurationUtil.getConfigurationEntry(MONGO_DB));
            return database.getCollection(collectionName);
        } catch (Exception e) {
            log.error("Ошибка при получении коллекции MongoDB: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении коллекции MongoDB", e);
        }
    }

    // Закрытие клиента при завершении работы
    public void close() {
        try {
            mongoClient.close();
        } catch (Exception e) {
            log.error("Ошибка при закрытии соединения с MongoDB: {}", e.getMessage());
            throw new RuntimeException("Ошибка при закрытии соединения с MongoDB", e);
        }
    }

    /**
     * Преобразует объект Java в MongoDB Document.
     * @param object Объект для преобразования.
     * @return Документ MongoDB.
     */
    public static Document toDocument(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            Document document = Document.parse(json);
            document.remove("id");
            return document;
        } catch (JsonProcessingException e) {
            log.error("Ошибка при преобразовании объекта в документ: {}", object, e);
            throw new IllegalStateException("Не удалось преобразовать объект в документ.", e);
        }
    }

    /**
     * Преобразует MongoDB Document в объект Java.
     * @param document Документ MongoDB.
     * @param clazz    Класс объекта.
     * @param <T>      Тип объекта.
     * @return Объект Java.
     */
    public static  <T> T fromDocument(Document document, Class<T> clazz) {
        try {
            // Убираем _id, заменяем его на id
            if (document.containsKey("_id")) {
                document.put("id", document.getObjectId("_id").toHexString());
                document.remove("_id");
            }
            String json = document.toJson();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Ошибка при преобразовании документа в объект: {}", document, e);
            throw new IllegalStateException("Не удалось преобразовать документ в объект.", e);
        }
    }
}