package repositories;

import model.HistoryContent;
import org.junit.jupiter.api.Test;
import providers.MongoDataProvider;
import utils.MongoDBUtil;

public class HistoryObjectsRepositoryTest {
    @Test
    public void testSaveONE() {
        try {
            HistoryObjectsRepository repository = HistoryObjectsRepository.defaultMongoRepository(
                    MongoDBUtil.getCollection("myDatabase", "history")
            );
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
