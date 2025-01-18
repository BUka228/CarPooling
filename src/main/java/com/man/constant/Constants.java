package com.man.constant;

public class Constants {

    // Пути и настройки
    public static final String DEFAULT_CONFIG_PATH = "./src/main/resources/environment.properties";
    public static final String WORKING_DIRECTORY = "working.directory";
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

    // SQL-запросы для работы с бронированиями
    public static final String CREATE_BOOKING_SQL = "INSERT INTO bookings (id, seat_count, status, booking_date, passport_number, passport_expiry_date, trip_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_BOOKING_BY_ID_SQL = "SELECT * FROM bookings WHERE id = ?";
    public static final String UPDATE_BOOKING_SQL = "UPDATE bookings SET seat_count = ?, status = ?, booking_date = ?, passport_number = ?, passport_expiry_date = ?, trip_id = ?, user_id = ? WHERE id = ?";
    public static final String DELETE_BOOKING_SQL = "DELETE FROM bookings WHERE id = ?";

    // SQL-запросы для работы с рейтингами
    public static final String CREATE_RATING_SQL = "INSERT INTO ratings (id, rating, comment, date, trip_id) VALUES (?, ?, ?, ?, ?)";
    public static final String GET_RATING_BY_ID_SQL = "SELECT * FROM ratings WHERE id = ?";
    public static final String UPDATE_RATING_SQL = "UPDATE ratings SET rating = ?, comment = ?, date = ?, trip_id = ? WHERE id = ?";
    public static final String DELETE_RATING_SQL = "DELETE FROM ratings WHERE id = ?";

    // SQL-запросы для работы с маршрутами
    public static final String SQL_CREATE_ROUTE = "INSERT INTO routes (id, start_point, end_point, date, estimated_duration) VALUES (?, ?, ?, ?, ?)";
    public static final String SQL_GET_ROUTE_BY_ID = "SELECT * FROM routes WHERE id = ?";
    public static final String SQL_UPDATE_ROUTE = "UPDATE routes SET start_point = ?, end_point = ?, date = ?, estimated_duration = ? WHERE id = ?";
    public static final String SQL_DELETE_ROUTE = "DELETE FROM routes WHERE id = ?";

    // SQL-запросы для работы с поездками
    public static final String CREATE_TRIP_SQL = "INSERT INTO trips (id, departure_time, max_passengers, creation_date, status, editable, user_id, route_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_TRIP_BY_ID_SQL = "SELECT * FROM trips WHERE id = ?";
    public static final String UPDATE_TRIP_SQL = "UPDATE trips SET departure_time = ?, max_passengers = ?, creation_date = ?, status = ?, editable = ?, user_id = ?, route_id = ? WHERE id = ?";
    public static final String DELETE_TRIP_SQL = "DELETE FROM trips WHERE id = ?";

    // SQL-запросы для работы с пользователями
    public static final String CREATE_USER_SQL = """
        INSERT INTO users (id, name, email, password, gender, phone, birth_date, address, preferences)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    public static final String GET_USER_BY_ID_SQL = """
        SELECT * FROM users WHERE id = ?
    """;

    public static final String UPDATE_USER_SQL = """
        UPDATE users
        SET name = ?, email = ?, password = ?, gender = ?, phone = ?, birth_date = ?, address = ?, preferences = ?
        WHERE id = ?
    """;

    public static final String DELETE_USER_SQL = """
        DELETE FROM users WHERE id = ?
    """;

    // Прочие константы
    public static final String PLANETS = "planets";
    public static final String MONTHS = "months";
    public static final String SYSTEM = "system";

    private Constants() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}