package dao.ratingdao;

import com.carpooling.dao.base.RatingDao;
import com.carpooling.dao.postgres.PostgresRatingDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PostgresRatingDaoTest extends AbstractRatingDaoTest {

    private SessionFactory sessionFactory;

    @Override
    protected RatingDao createRatingDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        return new PostgresRatingDao(sessionFactory);
    }

    @Override
    protected void cleanUp() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}