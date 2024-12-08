package utils;

import com.man.Constants;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;

public class MongoDBUtil {

    private static final MongoClient mongoClient;

    static {
        try {
            mongoClient = MongoClients.create(
                    ConfigurationUtil.getConfigurationEntry(Constants.MONGO_URI)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получение MongoDatabase по названию базы данных.
     */
    public static MongoDatabase getDatabase(String dbName) {
        return mongoClient.getDatabase(dbName);
    }

    /**
     * Получение коллекции MongoCollection по имени базы данных и коллекции.
     */
    public static MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = getDatabase(dbName);
        return database.getCollection(collectionName);
    }
}