package business.sevice.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseMongoTest {

    @Container
    protected static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    protected static MongoClient mongoClient;
    protected static MongoDatabase database;

    @BeforeAll
    static void setup() {
        // Устанавливаем подключение к MongoDB
        mongoClient = MongoClients.create(mongo.getConnectionString());
        database = mongoClient.getDatabase("testdb");
    }
}