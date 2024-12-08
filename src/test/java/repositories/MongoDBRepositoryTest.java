package repositories;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Status;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
public class MongoDBRepositoryTest {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;
    private MongoDBRepository<TestEntity> repository;


    @BeforeAll
    static void setUpAll() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        MongoDatabase database = mongoClient.getDatabase("testdb");
        collection = database.getCollection("testcollection", Document.class);
    }

    @AfterAll
    static void tearDownAll() {
        mongoClient.close();
        mongoDBContainer.stop();
    }

    @BeforeEach
    void setUp() {
        collection.deleteMany(new Document()); // Clear the collection before each test
        repository = new MongoDBRepository<>(TestEntity.class, collection);
    }


    @Test
    void testSave() {
        TestEntity entity = new TestEntity("testId", "testName");
        repository.save(entity);
        assertEquals(1, collection.countDocuments()); // Check if one document was inserted

        Document savedDocument = collection.find(new Document("id", "testId")).first();
        assertEquals("testId", savedDocument.getString("id"));
        assertEquals("testName", savedDocument.getString("name"));
    }


    @Test
    void testSaveAll() {
        List<TestEntity> entities = List.of(new TestEntity("1", "Name1"), new TestEntity("2", "Name2"));
        repository.saveAll(entities);
        assertEquals(2, collection.countDocuments());
    }


    @Test
    void testFindAll() {
        TestEntity entity1 = new TestEntity(
                "1",
                "Name1",
                Map.of("key1", "value1", "key2", 123)
        );
        TestEntity entity2 = new TestEntity(
                "2",
                "Name2",
                Map.of("key2", 123, "key1", "value1")
        );
        try {
            repository.save(entity1);
            repository.save(entity2);

            List<TestEntity> retrievedEntities = repository.findAll();

            assertEquals(2, retrievedEntities.size());

            assertEquals("1", retrievedEntities.get(0).getId());
            assertEquals("Name1", retrievedEntities.get(0).getName());


            assertEquals("2", retrievedEntities.get(1).getId());
            assertEquals("Name2", retrievedEntities.get(1).getName());

        } catch (Exception e) {
            log.error("Error in testFindAll: {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }



    @Test
    void testDeleteAll() {
        repository.save(new TestEntity("1", "Test"));
        repository.deleteAll();
        assertEquals(0, collection.countDocuments());
    }


    private static class TestEntity {
        private String id;
        private String name;
        private Map<String, Object> object;

        public TestEntity() {}

        public TestEntity(String id, String name, Map<String, Object> object) {
            this.id = id;
            this.name = name;
            this.object = object;
        }

        public TestEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        public Map<String, Object> getObject() {
            return object;
        }



        @Override
        public String toString() {
            return "TestEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}