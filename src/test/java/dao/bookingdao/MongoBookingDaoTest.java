package dao.bookingdao;

import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.mongo.MongoBookingDao;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MongoBookingDaoTest extends AbstractBookingDaoTest {

    @Mock
    private MongoCollection<Document> collection;

    @Override
    protected BookingDao createBookingDao() {
        MockitoAnnotations.openMocks(this);
        return new MongoBookingDao(collection);
    }

    @Override
    protected void cleanUp() {
        // Очистка не требуется для мок-объектов
    }
}