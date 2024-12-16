package providers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import converters.GenericConverter;
import model.HistoryContentTest;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoDataProviderTest {
    private static MongoDBContainer mongoContainer;
    private MongoDataProvider<HistoryContentTest> dataProvider;
    private MongoCollection<Document> collection;

    @Mock
    private GenericConverter<HistoryContentTest, Document> mockConverter;

    @BeforeAll
    static void startMongoContainer() {
        mongoContainer = new MongoDBContainer("mongo:6.0");
        mongoContainer.start();
    }

    @AfterAll
    static void stopMongoContainer() {
        mongoContainer.stop();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String connectionString = mongoContainer.getConnectionString();
        var mongoClient = com.mongodb.client.MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("test");
        collection = database.getCollection("history");

        dataProvider = new MongoDataProvider<>(collection, mockConverter);
    }

    @Test
    void testSaveRecord_Success() {
        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");
        Document mockDocument = new Document("_id", "1")
                .append("actor", "user")
                .append("action", "CREATE")
                .append("content", "Sample Content");

        try {
            when(mockConverter.serialize(content)).thenReturn(mockDocument);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() -> dataProvider.saveRecord(content));
        assertEquals(1, collection.countDocuments(), "Запись не была сохранена в MongoDB");
    }

    @Test
    void testDeleteRecord_Success() {
        Document mockDocument = new Document("_id", "1")
                .append("actor", "user")
                .append("action", "CREATE")
                .append("content", "Sample Content");
        collection.insertOne(mockDocument);

        HistoryContentTest content = new HistoryContentTest("1", "user", "CREATE", "Sample Content");

        when(mockConverter.getId(content)).thenReturn("1");

        assertDoesNotThrow(() -> dataProvider.deleteRecord(content));
        assertEquals(0, collection.countDocuments(), "Запись не была удалена из MongoDB");
    }

    @Test
    void testGetRecordById_Success() {
        Document mockDocument = new Document("_id", "1")
                .append("actor", "user")
                .append("action", "CREATE")
                .append("content", "Sample Content");
        collection.insertOne(mockDocument);

        HistoryContentTest expectedContent = new HistoryContentTest("1", "user", "CREATE", "Sample Content");
        try {
            when(mockConverter.deserialize(mockDocument)).thenReturn(expectedContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HistoryContentTest result = dataProvider.getRecordById("1");

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(expectedContent.getId(), result.getId(), "ID не совпадает");
        assertEquals(expectedContent.getActor(), result.getActor(), "Actor не совпадает");
        assertEquals(expectedContent.getAction(), result.getAction(), "Action не совпадает");
        assertEquals(expectedContent.getContent(), result.getContent(), "Content не совпадает");
    }

    @Test
    void testGetAllRecords_Success() {
        Document mockDocument1 = new Document("_id", "1")
                .append("actor", "user1")
                .append("action", "CREATE")
                .append("content", "Content1");
        Document mockDocument2 = new Document("_id", "2")
                .append("actor", "user2")
                .append("action", "UPDATE")
                .append("content", "Content2");
        collection.insertOne(mockDocument1);
        collection.insertOne(mockDocument2);

        HistoryContentTest content1 = new HistoryContentTest("1", "user1", "CREATE", "Content1");
        HistoryContentTest content2 = new HistoryContentTest("2", "user2", "UPDATE", "Content2");

        try {
            when(mockConverter.deserialize(mockDocument1)).thenReturn(content1);
            when(mockConverter.deserialize(mockDocument2)).thenReturn(content2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        List<HistoryContentTest> results = dataProvider.getAllRecords();

        assertNotNull(results, "Список записей не должен быть null");
        assertEquals(2, results.size(), "Количество записей не совпадает");
        assertTrue(results.contains(content1), "Список не содержит первую запись");
        assertTrue(results.contains(content2), "Список не содержит вторую запись");
    }
}
