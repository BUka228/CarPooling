package util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@Slf4j
public class MongoDbUtilTest {


    private static final String DB_NAME = "testDb";
    private static final String COLLECTION_NAME = "testCollection";
    @Test
    public void testGetDatabase() {

        MongoDatabase mockDatabase;
        MongoDatabase result;
        try (MongoClient mockMongoClient = Mockito.mock(MongoClient.class)) {
            mockDatabase = Mockito.mock(MongoDatabase.class);
            when(mockMongoClient.getDatabase(DB_NAME)).thenReturn(mockDatabase);

            result = mockMongoClient.getDatabase(DB_NAME);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        assertEquals(mockDatabase, result);
    }

    @Test
    public void testGetCollection() {

        MongoCollection<Document> mockCollection;
        MongoCollection<Document> result;
        try (MongoClient mockMongoClient = Mockito.mock(MongoClient.class)) {
            MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);
            mockCollection = Mockito.mock(MongoCollection.class);

            when(mockMongoClient.getDatabase(DB_NAME)).thenReturn(mockDatabase);
            when(mockDatabase.getCollection(COLLECTION_NAME, Document.class)).thenReturn(mockCollection);


            result = mockMongoClient
                    .getDatabase(DB_NAME)
                    .getCollection(COLLECTION_NAME, Document.class);
            assertEquals(mockCollection, result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}