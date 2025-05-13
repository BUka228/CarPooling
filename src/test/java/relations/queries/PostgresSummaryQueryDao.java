package relations.queries;

import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.entities.database.Trip; // Нужен для Criteria API
import com.carpooling.entities.database.User; // Нужен для Criteria API
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import jakarta.persistence.criteria.*; // Для Criteria API
import java.util.ArrayList;
import java.util.List;

public class PostgresSummaryQueryDao implements SummaryQueryDao {

    private final SessionFactory sessionFactory;

    public PostgresSummaryQueryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<UserTripCountDto> getUserTripCountsHql() throws DataAccessException {
        try {
            String hql = "SELECT new relations.queries.UserTripCountDto(u.name, COUNT(t.id)) " +
                    "FROM User u LEFT JOIN u.trips t " + // u.trips - это коллекция в User
                    "GROUP BY u.id, u.name " +
                    "ORDER BY u.name";
            Query<UserTripCountDto> query = getCurrentSession().createQuery(hql, UserTripCountDto.class);
            return query.list();
        } catch (Exception e) {
            throw new DataAccessException("Error executing HQL for user trip counts", e);
        }
    }

    @Override
    public List<UserTripCountDto> getUserTripCountsCriteria() throws DataAccessException {
        try {
            CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<UserTripCountDto> cq = cb.createQuery(UserTripCountDto.class);
            Root<User> userRoot = cq.from(User.class);

            Join<User, Trip> tripJoin = userRoot.join("trips", JoinType.LEFT);

            cq.select(cb.construct(
                    UserTripCountDto.class,
                    userRoot.get("name"),
                    cb.count(tripJoin.get("id")) // Считаем ID поездок
            ));

            cq.groupBy(userRoot.get("id"), userRoot.get("name"));
            cq.orderBy(cb.asc(userRoot.get("name")));

            Query<UserTripCountDto> query = getCurrentSession().createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new DataAccessException("Error executing Criteria API for user trip counts", e);
        }
    }

    @Override
    public List<UserTripCountDto> getUserTripCountsNativeSql() throws DataAccessException {
        try {

            String sql = "SELECT u.name AS userName, COUNT(t.id) AS tripCount " +
                    "FROM users u LEFT JOIN trips t ON u.id = t.user_id " +
                    "GROUP BY u.id, u.name " +
                    "ORDER BY u.name";


            // NativeQuery<Object[]>
            NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(sql, Object[].class);
            // Если бы мы использовали addScalar или setResultTransformer, код был бы другим.

            List<Object[]> results = query.list();
            List<UserTripCountDto> dtos = new ArrayList<>();
            for (Object[] row : results) {
                String userName = (String) row[0];
                Number tripCountNumber = (Number) row[1];
                Long tripCount = tripCountNumber != null ? tripCountNumber.longValue() : 0L;
                dtos.add(new UserTripCountDto(userName, tripCount));
            }
            return dtos;
        } catch (Exception e) {
            throw new DataAccessException("Error executing Native SQL for user trip counts", e);
        }
    }
}