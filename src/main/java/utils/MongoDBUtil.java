package utils;

import com.man.Constants;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.io.IOException;

@Slf4j
public class MongoDBUtil {

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
    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        try {
            MongoDatabase database = getDatabase(dbName);
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
}