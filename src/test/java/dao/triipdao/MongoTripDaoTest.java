package dao.triipdao;

import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.mongo.MongoTripDao;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MongoTripDaoTest extends AbstractTripDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Override
    protected TripDao createTripDao() {
        MockitoAnnotations.openMocks(this);
        return new MongoTripDao(collection);
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется для мок-объектов
    }
}