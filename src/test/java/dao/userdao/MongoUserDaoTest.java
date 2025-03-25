package dao.userdao;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.mongo.MongoUserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MongoUserDaoTest extends AbstractUserDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Override
    protected UserDao createUserDao() {
        MockitoAnnotations.openMocks(this);
        return new MongoUserDao(collection);
    }

    @Override
    protected void cleanUp() {}
}