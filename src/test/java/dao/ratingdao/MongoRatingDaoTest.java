package dao.ratingdao;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.mongo.MongoRatingDao;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MongoRatingDaoTest extends AbstractRatingDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Override
    protected RatingDao createRatingDao() {
        MockitoAnnotations.openMocks(this);
        return new MongoRatingDao(collection);
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется для мок-объектов
    }
}