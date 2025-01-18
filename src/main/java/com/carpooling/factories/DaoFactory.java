package com.carpooling.factories;

import com.carpooling.cli.context.CliContext;
import com.carpooling.dao.base.*;
import com.carpooling.dao.csv.*;
import com.carpooling.dao.mongo.*;
import com.carpooling.dao.postgres.*;
import com.carpooling.dao.xml.*;
import com.carpooling.utils.MongoDBUtil;
import com.carpooling.utils.PostgresConnectionUtil;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static com.carpooling.constants.Constants.*;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;
import static com.carpooling.utils.ConfigurationUtil.getConfigurationEntry;



@Slf4j
public class DaoFactory {

    @NotNull
    @Contract("_ -> new")
    public static UserDao getUserDao(@NotNull CliContext.StorageType storageType) {
        log.info(INIT_DAO_START, storageType);
        try {
            UserDao userDao = switch (storageType) {
                case XML -> {
                    String xmlFilePath = getConfigurationEntry(XML_FILE_PATH);
                    yield new XmlUserDao(xmlFilePath + USERS_XML);
                }
                case CSV -> {
                    String csvFilePath = getConfigurationEntry(CSV_FILE_PATH);
                    yield new CsvUserDao(csvFilePath + USERS_CSV);
                }
                case MONGO -> {
                    MongoCollection<Document> collection = new MongoDBUtil().getCollection(MONGO_COLLECTION_USERS);
                    yield new MongoUserDao(collection);
                }
                case POSTGRES -> {
                    Connection connection = PostgresConnectionUtil.getConnection();
                    yield new PostgresUserDao(connection);
                }
            };
            log.info(INIT_DAO_SUCCESS, storageType);
            return userDao;
        } catch (IOException | SQLException e) {
            log.error(INIT_DAO_ERROR, storageType, e);
            throw new RuntimeException(ERROR_INIT_USER_DAO, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static TripDao getTripDao(@NotNull CliContext.StorageType storageType) {
        log.info(INIT_DAO_START, storageType);
        try {
            TripDao tripDao = switch (storageType) {
                case XML -> {
                    String xmlFilePath = getConfigurationEntry(XML_FILE_PATH);
                    yield new XmlTripDao(xmlFilePath + TRIPS_XML);
                }
                case CSV -> {
                    String csvFilePath = getConfigurationEntry(CSV_FILE_PATH);
                    yield new CsvTripDao(csvFilePath + TRIPS_CSV);
                }
                case MONGO -> {
                    MongoCollection<Document> collection = new MongoDBUtil().getCollection(MONGO_COLLECTION_TRIPS);
                    yield new MongoTripDao(collection);
                }
                case POSTGRES -> {
                    Connection connection = PostgresConnectionUtil.getConnection();
                    yield new PostgresTripDao(connection);
                }
            };
            log.info(INIT_DAO_SUCCESS, storageType);
            return tripDao;
        } catch (IOException | SQLException e) {
            log.error(INIT_DAO_ERROR, storageType, e);
            throw new RuntimeException(ERROR_INIT_TRIP_DAO, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static RouteDao getRouteDao(@NotNull CliContext.StorageType storageType) {
        log.info(INIT_DAO_START, storageType);
        try {
            RouteDao routeDao = switch (storageType) {
                case XML -> {
                    String xmlFilePath = getConfigurationEntry(XML_FILE_PATH);
                    yield new XmlRouteDao(xmlFilePath + ROUTES_XML);
                }
                case CSV -> {
                    String csvFilePath = getConfigurationEntry(CSV_FILE_PATH);
                    yield new CsvRouteDao(csvFilePath + ROUTES_CSV);
                }
                case MONGO -> {
                    MongoCollection<Document> collection = new MongoDBUtil().getCollection(MONGO_COLLECTION_ROUTES);
                    yield new MongoRouteDao(collection);
                }
                case POSTGRES -> {
                    Connection connection = PostgresConnectionUtil.getConnection();
                    yield new PostgresRouteDao(connection);
                }
            };
            log.info(INIT_DAO_SUCCESS, storageType);
            return routeDao;
        } catch (IOException | SQLException e) {
            log.error(INIT_DAO_ERROR, storageType, e);
            throw new RuntimeException(ERROR_INIT_ROUTE_DAO, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static BookingDao getBookingDao(@NotNull CliContext.StorageType storageType) {
        log.info(INIT_DAO_START, storageType);
        try {
            BookingDao bookingDao = switch (storageType) {
                case XML -> {
                    String xmlFilePath = getConfigurationEntry(XML_FILE_PATH);
                    yield new XmlBookingDao(xmlFilePath + BOOKINGS_XML);
                }
                case CSV -> {
                    String csvFilePath = getConfigurationEntry(CSV_FILE_PATH);
                    yield new CsvBookingDao(csvFilePath + BOOKINGS_CSV);
                }
                case MONGO -> {
                    MongoCollection<Document> collection = new MongoDBUtil().getCollection(MONGO_COLLECTION_BOOKINGS);
                    yield new MongoBookingDao(collection);
                }
                case POSTGRES -> {
                    Connection connection = PostgresConnectionUtil.getConnection();
                    yield new PostgresBookingDao(connection);
                }
            };
            log.info(INIT_DAO_SUCCESS, storageType);
            return bookingDao;
        } catch (IOException | SQLException e) {
            log.error(INIT_DAO_ERROR, storageType, e);
            throw new RuntimeException(ERROR_INIT_BOOKING_DAO, e);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static RatingDao getRatingDao(@NotNull CliContext.StorageType storageType) {
        log.info(INIT_DAO_START, storageType);
        try {
            RatingDao ratingDao = switch (storageType) {
                case XML -> {
                    String xmlFilePath = getConfigurationEntry(XML_FILE_PATH);
                    yield new XmlRatingDao(xmlFilePath + RATINGS_XML);
                }
                case CSV -> {
                    String csvFilePath = getConfigurationEntry(CSV_FILE_PATH);
                    yield new CsvRatingDao(csvFilePath + RATINGS_CSV);
                }
                case MONGO -> {
                    MongoCollection<Document> collection = new MongoDBUtil().getCollection(MONGO_COLLECTION_RATINGS);
                    yield new MongoRatingDao(collection);
                }
                case POSTGRES -> {
                    Connection connection = PostgresConnectionUtil.getConnection();
                    yield new PostgresRatingDao(connection);
                }
            };
            log.info(INIT_DAO_SUCCESS, storageType);
            return ratingDao;
        } catch (IOException | SQLException e) {
            log.error(INIT_DAO_ERROR, storageType, e);
            throw new RuntimeException(ERROR_INIT_RATING_DAO, e);
        }
    }
}