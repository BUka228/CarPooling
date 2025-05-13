package relations.queries;

import com.carpooling.entities.database.Address; // Для создания User
import com.carpooling.entities.database.Route;
import com.carpooling.entities.database.Trip;
import com.carpooling.entities.database.User;
import com.carpooling.entities.enums.TripStatus;
import inheritance.common.GenericDao; // Используем наш GenericDao
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil;
import com.carpooling.hibernate.ThreadLocalSessionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Summary Queries (HQL, Criteria, NativeSQL) Tests")
class SummaryQueriesTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    private SummaryQueryDao summaryQueryDao;
    // DAO для подготовки данных
    private GenericDao<User, java.util.UUID> userDao;
    private GenericDao<Route, java.util.UUID> routeDao;
    private GenericDao<Trip, java.util.UUID> tripDao;


    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }


    @BeforeEach
    void setUp() {
        summaryQueryDao = new PostgresSummaryQueryDao(sessionFactory);
        userDao = new GenericDao<>(sessionFactory, User.class);
        routeDao = new GenericDao<>(sessionFactory, Route.class);
        tripDao = new GenericDao<>(sessionFactory, Trip.class);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();

        prepareTestData();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            ThreadLocalSessionContext.unbind();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    void prepareTestData() {
        User user1 = new User();
        user1.setName("Alice Driver");
        user1.setEmail("alice@example.com");
        user1.setPassword("pass");
        user1.setBirthDate(LocalDate.now().minusYears(30));
        user1.setAddress(new Address("1 St", "111", "CityA"));
        userDao.save(user1);

        User user2 = new User();
        user2.setName("Bob Rider");
        user2.setEmail("bob@example.com");
        user2.setPassword("pass");
        user2.setBirthDate(LocalDate.now().minusYears(25));
        user2.setAddress(new Address("2 St", "222", "CityB"));
        userDao.save(user2);

        User user3 = new User();
        user3.setName("Charlie NoTrips");
        user3.setEmail("charlie@example.com");
        user3.setPassword("pass");
        user3.setBirthDate(LocalDate.now().minusYears(40));
        user3.setAddress(new Address("3 St", "333", "CityC"));
        userDao.save(user3);


        Route route1 = new Route();
        route1.setStartingPoint("A"); route1.setEndingPoint("B");
        routeDao.save(route1);

        // Alice - 2 trips
        Trip trip1_1 = new Trip();
        trip1_1.setUser(user1); trip1_1.setRoute(route1); trip1_1.setDepartureTime(LocalDateTime.now().plusDays(1));
        trip1_1.setMaxPassengers((byte)3); trip1_1.setStatus(TripStatus.PLANNED);
        tripDao.save(trip1_1);

        Trip trip1_2 = new Trip();
        trip1_2.setUser(user1); trip1_2.setRoute(route1); trip1_2.setDepartureTime(LocalDateTime.now().plusDays(2));
        trip1_2.setMaxPassengers((byte)4); trip1_2.setStatus(TripStatus.PLANNED);
        tripDao.save(trip1_2);

        // Bob - 1 trip
        Trip trip2_1 = new Trip();
        trip2_1.setUser(user2); trip2_1.setRoute(route1); trip2_1.setDepartureTime(LocalDateTime.now().plusDays(3));
        trip2_1.setMaxPassengers((byte)2); trip2_1.setStatus(TripStatus.PLANNED);
        tripDao.save(trip2_1);

        session.flush();
        session.clear();
    }

    private void runAndAssertQueries(String queryType, List<UserTripCountDto> results) {
        System.out.println("\nResults from " + queryType + ":");
        results.forEach(dto -> System.out.println(dto.getUserName() + ": " + dto.getTripCount()));

        assertThat(results).isNotNull().hasSize(3); // Включая Charlie NoTrips

        assertThat(results).anySatisfy(dto -> {
            assertThat(dto.getUserName()).isEqualTo("Alice Driver");
            assertThat(dto.getTripCount()).isEqualTo(2L);
        });
        assertThat(results).anySatisfy(dto -> {
            assertThat(dto.getUserName()).isEqualTo("Bob Rider");
            assertThat(dto.getTripCount()).isEqualTo(1L);
        });
        assertThat(results).anySatisfy(dto -> {
            assertThat(dto.getUserName()).isEqualTo("Charlie NoTrips");
            assertThat(dto.getTripCount()).isEqualTo(0L);
        });
    }

    @Test
    @DisplayName("Get User Trip Counts: HQL")
    void testGetUserTripCounts_HQL() {
        long startTime = System.nanoTime();
        List<UserTripCountDto> results = summaryQueryDao.getUserTripCountsHql();
        long endTime = System.nanoTime();
        System.out.println("HQL Execution Time: " + (endTime - startTime) / 1_000_000.0 + " ms");
        runAndAssertQueries("HQL", results);
    }

    @Test
    @DisplayName("Get User Trip Counts: Criteria API")
    void testGetUserTripCounts_Criteria() {
        long startTime = System.nanoTime();
        List<UserTripCountDto> results = summaryQueryDao.getUserTripCountsCriteria();
        long endTime = System.nanoTime();
        System.out.println("Criteria API Execution Time: " + (endTime - startTime) / 1_000_000.0 + " ms");
        runAndAssertQueries("Criteria API", results);
    }

    @Test
    @DisplayName("Get User Trip Counts: Native SQL")
    void testGetUserTripCounts_NativeSql() {
        long startTime = System.nanoTime();
        List<UserTripCountDto> results = summaryQueryDao.getUserTripCountsNativeSql();
        long endTime = System.nanoTime();
        System.out.println("Native SQL Execution Time: " + (endTime - startTime) / 1_000_000.0 + " ms");
        runAndAssertQueries("Native SQL", results);
    }
}