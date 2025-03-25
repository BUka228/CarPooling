package dao.bookingdao;


import com.carpooling.dao.base.BookingDao;
import com.carpooling.dao.postgres.PostgresBookingDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PostgresBookingDaoTest extends AbstractBookingDaoTest {

    private SessionFactory sessionFactory;

    @Override
    protected BookingDao createBookingDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        return new PostgresBookingDao(sessionFactory);
    }

    @Override
    protected void cleanUp() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}