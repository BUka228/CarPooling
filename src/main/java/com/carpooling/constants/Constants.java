package com.carpooling.constants;

public class Constants {

    // Пути и настройки
    public static final String DEFAULT_CONFIG_PATH = "./src/main/resources/environment.properties";
    public static final String XML_FILE_PATH = "xml.file.path";
    public static final String CSV_FILE_PATH = "csv.file.path";

    // Настройки базы данных
    public static final String DB_URL = "db.url";
    public static final String DB_USER = "db.user";
    public static final String DB_PASSWORD = "db.password";

    // Настройки MongoDB
    public static final String MONGO_URI = "mongodb.uri";
    public static final String MONGO_DB = "mongodb.db";
    public static final String MONGO_COLLECTION = "mongodb.collection";
    public static final String MONGO_COLLECTION_USERS = "collections.users";
    public static final String MONGO_COLLECTION_TRIPS = "collections.trips";
    public static final String MONGO_COLLECTION_ROUTES = "collections.routes";
    public static final String MONGO_COLLECTION_BOOKINGS = "collections.bookings";
    public static final String MONGO_COLLECTION_RATINGS = "collections.ratings";

    // Идентификаторы
    public static final String USER_ID = "userId";
    public static final String ROUTE_ID = "routeId";
    public static final String TRIP_ID = "tripId";
    public static final String MONGO_ID = "_id";

    // Названия DAO
    public static final String USER_DAO = "User";
    public static final String TRIP_DAO = "Trip";
    public static final String ROUTE_DAO = "Route";
    public static final String BOOKING_DAO = "Booking";
    public static final String RATING_DAO = "Rating";


    // Названия файлов
    public static final String USERS_XML = "users.xml";
    public static final String TRIPS_XML = "trips.xml";
    public static final String ROUTES_XML = "routes.xml";
    public static final String BOOKINGS_XML = "bookings.xml";
    public static final String RATINGS_XML = "ratings.xml";

    public static final String USERS_CSV = "users.csv";
    public static final String TRIPS_CSV = "trips.csv";
    public static final String ROUTES_CSV = "routes.csv";
    public static final String BOOKINGS_CSV = "bookings.csv";
    public static final String RATINGS_CSV = "ratings.csv";

    public static final String PREF_NODE_NAME = "com/carpooling/cli";
    public static final String STORAGE_TYPE_KEY = "storageType";
    public static final String USER_ID_KEY = "currentUserId";



    // --- Booking HQL ---
    public static final String COUNT_BOOKED_SEATS_HQL = "SELECT COALESCE(SUM(b.numberOfSeats), 0) FROM Booking b WHERE b.trip.id = :tripId"; // Используем COALESCE для 0, если нет броней
    public static final String FIND_BOOKINGS_BY_USER_HQL = "FROM Booking b LEFT JOIN FETCH b.trip LEFT JOIN FETCH b.trip.route WHERE b.user.id = :userId ORDER BY b.bookingDate DESC";
    public static final String FIND_BOOKING_BY_USER_AND_TRIP_HQL = "FROM Booking b WHERE b.user.id = :userId AND b.trip.id = :tripId";
    public static final String FIND_BOOKING_BY_ID_WITH_DETAILS_HQL =
            "FROM Booking b LEFT JOIN FETCH b.trip LEFT JOIN FETCH b.user WHERE b.id = :bookingId";

    // --- Rating HQL ---
    public static final String FIND_RATING_BY_USER_AND_TRIP_HQL =
            "FROM Rating r JOIN FETCH r.trip t JOIN FETCH t.user u WHERE u.id = :userId AND t.id = :tripId";
    // --- Trip HQL ---
    public static final String GET_TRIP_BY_ID_WITH_DETAILS_HQL = "FROM Trip t LEFT JOIN FETCH t.route LEFT JOIN FETCH t.user WHERE t.id = :tripId";
    public static final String FIND_TRIPS_HQL_BASE = "SELECT DISTINCT t FROM Trip t JOIN FETCH t.route r WHERE 1=1"; // Добавил DISTINCT
    public static final String FIND_TRIPS_HQL_START_POINT = " AND LOWER(r.startingPoint) LIKE LOWER(:startPoint)";
    public static final String FIND_TRIPS_HQL_END_POINT = " AND LOWER(r.endingPoint) LIKE LOWER(:endPoint)";
    public static final String FIND_TRIPS_HQL_DATE_RANGE = " AND t.departureTime >= :startDate AND t.departureTime < :endDate";
    public static final String FIND_TRIPS_HQL_ORDER_BY = " ORDER BY t.departureTime ASC";


    // --- Database Metadata Native SQL (PostgreSQL specific) ---
    public static final String GET_TABLE_NAMES_SQL = "SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'public' ORDER BY tablename";
    public static final String GET_TABLE_ROW_COUNT_SQL_TEMPLATE = "SELECT COUNT(*) FROM %s";

    public static final String GET_TABLE_COLUMN_INFO_SQL = "SELECT column_name, data_type FROM information_schema.columns " +
            "WHERE table_schema = 'public' AND table_name = :tableName ORDER BY ordinal_position";

    public static final String GET_DATABASE_SIZE_SQL = "SELECT pg_size_pretty(pg_database_size(current_database()))";



    public static final String DEFAULT_HIBERNATE_CONFIG_PATH = "hibernate.cfg.xml"; // Имя файла по умолчанию
    public static final String HIBERNATE_CONFIG_PROPERTY = "hibernate.config.file"; // Системное свойство


    private Constants() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}