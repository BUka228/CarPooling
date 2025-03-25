package dao.triipdao;

import com.carpooling.dao.base.TripDao;
import com.carpooling.dao.postgres.PostgresTripDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PostgresTripDaoTest extends AbstractTripDaoTest {

    private SessionFactory sessionFactory;

    @Override
    protected TripDao createTripDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        return new PostgresTripDao(sessionFactory);
    }

    @Override
    protected void cleanUp() {
        // Закрываем SessionFactory после каждого теста
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}