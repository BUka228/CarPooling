package dao.routedao;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.mongo.MongoRouteDao;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MongoRouteDaoTest extends AbstractRouteDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Override
    protected RouteDao createRouteDao() {
        MockitoAnnotations.openMocks(this);
        return new MongoRouteDao(collection);
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется для мок-объектов
    }
}