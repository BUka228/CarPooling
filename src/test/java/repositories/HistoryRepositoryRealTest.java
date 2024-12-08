package repositories;

import com.man.Constants;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import man.TestGener;
import model.HistoryContent;
import model.Status;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import utils.ConfigurationUtil;

import java.util.List;
import java.util.Map;

import static utils.MongoDBUtil.getCollection;

public class HistoryRepositoryRealTest extends TestGener {
    @Test
    public void testSaveHistoryONE() {
        try {
            HistoryRepository historyRepository = new HistoryRepository(
                    getCollection(
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
                    )
            );
            HistoryContent historyContent1 = new HistoryContent(
                    "testClass",
                    "testMethod",
                    Map.of("key1", "value1", "key2", 123),
                    Status.SUCCESS
            );
            historyRepository.save(historyContent1);
            log.info("История контента успешно сохранена: {}", historyContent1);
            //List<HistoryContent> historyContents = historyRepository.findAll();
            //log.info("Извлеченное содержимое истории: {}", historyContents);
        } catch (Exception e) {
            log.error("Ошибка в testSaveHistoryONE: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testSaveHistoryALL() {
        try {
            HistoryRepository historyRepository = new HistoryRepository(
                    getCollection(
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
                    )
            );
            List<HistoryContent> historyContents = List.of(
                    new HistoryContent(
                            "testClass",
                            "testMethod",
                            Map.of("key1", "value1", "key2", 123),
                            Status.SUCCESS
                    ),
                    new HistoryContent(
                            "testClass",
                            "testMethod",
                            Map.of("key2", 123, "key1", "value1"),
                            Status.SUCCESS
                    )
            );

            historyRepository.saveAll(historyContents);


        } catch (Exception e) {
            log.error("Ошибка в testSaveHistoryAll: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testFindAll() {
        try {
            HistoryRepository historyRepository = new HistoryRepository(
                    getCollection(
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_DB),
                            ConfigurationUtil.getConfigurationEntry(Constants.MONGO_COLLECTION)
                    )
            );
            List<HistoryContent> historyContents = historyRepository.findAll();
            log.info("Извлеченное содержимое истории: {}", historyContents);
        } catch (Exception e) {
            log.error("Ошибка в testFindAll: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
