package dao.routedao;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.dao.postgres.PostgresRouteDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PostgresRouteDaoTest extends AbstractRouteDaoTest {

    private SessionFactory sessionFactory;

    @Override
    protected RouteDao createRouteDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        return new PostgresRouteDao(sessionFactory);
    }

    @Override
    protected void cleanUp() {
        // Закрываем SessionFactory после каждого теста
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}