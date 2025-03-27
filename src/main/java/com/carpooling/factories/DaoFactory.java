package com.carpooling.factories;

import com.carpooling.cli.context.CliContext;
import com.carpooling.constants.Constants; // Импортируем класс Constants
import com.carpooling.dao.base.*;
import com.carpooling.dao.csv.*;
import com.carpooling.dao.mongo.*;
import com.carpooling.dao.postgres.*;
import com.carpooling.dao.xml.*;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.transaction.DataAccessManager;
import com.carpooling.transaction.HibernateDataAccessManager;
import com.carpooling.transaction.NoOpDataAccessManager;
import com.carpooling.utils.ConfigurationUtil;
import com.carpooling.utils.HibernateUtil;
import com.carpooling.utils.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

// Импортируем статически константы для имен DAO и ключей конфигурации
import static com.carpooling.constants.Constants.*;

@Slf4j
public class DaoFactory {

    // --- DaoContext ---
    public record DaoContext<D>(D dao, DataAccessManager dataAccessManager) {}

    // --- Фабричные методы для каждого DAO ---

    @NotNull
    public static DaoContext<UserDao> getUserDaoContext() {
        return createContext(USER_DAO, // Используем константы для имен
                XmlUserDao::new, USERS_XML,
                CsvUserDao::new, USERS_CSV,
                MongoUserDao::new, MONGO_COLLECTION_USERS,
                PostgresUserDao::new);
    }

    @NotNull
    public static DaoContext<TripDao> getTripDaoContext() {
        return createContext(TRIP_DAO,
                XmlTripDao::new, TRIPS_XML,
                CsvTripDao::new, TRIPS_CSV,
                MongoTripDao::new, MONGO_COLLECTION_TRIPS,
                PostgresTripDao::new);
    }

    @NotNull
    public static DaoContext<RouteDao> getRouteDaoContext() {
        return createContext(ROUTE_DAO,
                XmlRouteDao::new, ROUTES_XML,
                CsvRouteDao::new, ROUTES_CSV,
                MongoRouteDao::new, MONGO_COLLECTION_ROUTES,
                PostgresRouteDao::new);
    }

    @NotNull
    public static DaoContext<BookingDao> getBookingDaoContext() {
        return createContext(BOOKING_DAO,
                XmlBookingDao::new, BOOKINGS_XML,
                CsvBookingDao::new, BOOKINGS_CSV,
                MongoBookingDao::new, MONGO_COLLECTION_BOOKINGS,
                PostgresBookingDao::new);
    }

    @NotNull
    public static DaoContext<RatingDao> getRatingDaoContext() {
        return createContext(RATING_DAO,
                XmlRatingDao::new, RATINGS_XML,
                CsvRatingDao::new, RATINGS_CSV,
                MongoRatingDao::new, MONGO_COLLECTION_RATINGS,
                PostgresRatingDao::new);
    }

    // --- Общий Generic Метод Создания Контекста ---

    // Функциональные интерфейсы для конструкторов (можно вынести)
    @FunctionalInterface private interface FileDaoConstructor<D> { D apply(String filePath) throws Exception; }
    @FunctionalInterface private interface MongoDaoConstructor<D> { D apply(MongoCollection<Document> collection) throws Exception; }
    @FunctionalInterface private interface PostgresDaoConstructor<D> { D apply(SessionFactory factory) throws Exception; }

    @NotNull
    private static <D> DaoContext<D> createContext(
            String daoName, // Имя DAO для логов
            FileDaoConstructor<D> xmlConstructor, String xmlFileName,
            FileDaoConstructor<D> csvConstructor, String csvFileName,
            MongoDaoConstructor<D> mongoConstructor, String mongoCollectionKey,
            PostgresDaoConstructor<D> postgresConstructor)
    {
        CliContext.StorageType type = CliContext.getCurrentStorageType();
        log.debug("Creating DaoContext for {} with type: {}", daoName, type);
        try {
            DataAccessManager manager;
            D dao;

            switch (type) {
                case XML:
                    String xmlPath = ConfigurationUtil.getConfigurationEntry(XML_FILE_PATH);
                    dao = xmlConstructor.apply(xmlPath + xmlFileName);
                    manager = new NoOpDataAccessManager();
                    break;
                case CSV:
                    String csvPath = ConfigurationUtil.getConfigurationEntry(CSV_FILE_PATH);
                    dao = csvConstructor.apply(csvPath + csvFileName);
                    manager = new NoOpDataAccessManager();
                    break;
                case MONGO:
                    // Оптимизация: Создаем MongoDBUtil один раз, если возможно
                    MongoDBUtil mongoUtil = getMongoDBUtilInstance(); // Метод-хелпер ниже
                    String collectionName = ConfigurationUtil.getConfigurationEntry(mongoCollectionKey);
                    if (collectionName == null || collectionName.isBlank()) {
                        throw new DataAccessException("MongoDB collection name not configured for key: " + mongoCollectionKey);
                    }
                    MongoCollection<Document> collection = mongoUtil.getCollection(collectionName);
                    dao = mongoConstructor.apply(collection);
                    manager = new NoOpDataAccessManager();
                    break;
                case POSTGRES:
                    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                    dao = postgresConstructor.apply(sessionFactory);
                    manager = new HibernateDataAccessManager(sessionFactory);
                    break;
                default:
                    // Не должно произойти из-за enum, но для полноты
                    throw new IllegalStateException("Unsupported storage type: " + type);
            }

            log.info("Successfully initialized DAO Context for {} with storage type: {}", daoName, type);
            return new DaoContext<>(dao, manager);

        } catch (IOException e) { // Ловим IOException от ConfigurationUtil
            handleContextCreationError(daoName, type, e);
            throw new RuntimeException("Configuration error during DAO Context creation", e);
        } catch (Exception e) { // Ловим остальные ошибки (JAXB, Csv, Mongo, Hibernate)
            handleContextCreationError(daoName, type, e);
            throw new RuntimeException("Unhandled error creating DAO Context", e);
        }
    }

    private static void handleContextCreationError(String daoName, CliContext.StorageType type, Exception e) {
        log.error("Error initializing DAO Context for {} with type {}: {}", daoName, type, e.getMessage(), e);
        // Бросаем RuntimeException, чтобы остановить инициализацию ServiceFactory
        throw new RuntimeException("Failed to initialize " + daoName + " DAO infrastructure", e);
    }


    private static MongoDBUtil mongoDBUtilInstance;
    private static MongoDBUtil getMongoDBUtilInstance() {
        if (mongoDBUtilInstance == null) {
            log.debug("Creating MongoDBUtil instance.");
            mongoDBUtilInstance = new MongoDBUtil();
        }
        return mongoDBUtilInstance;
    }
}