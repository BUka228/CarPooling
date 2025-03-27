package com.carpooling.factories;

import com.carpooling.dao.base.*;
import com.carpooling.services.base.*;
import com.carpooling.services.impl.*;

/**
 * Фабрика для получения экземпляров сервисов (синглтонов).
 * Инициализирует сервисы с необходимыми DAO и TransactionManager.
 */
public class ServiceFactory {

    // Контексты для каждого DAO
    private static final DaoFactory.DaoContext<UserDao> USER_CONTEXT = DaoFactory.getUserDaoContext();
    private static final DaoFactory.DaoContext<TripDao> TRIP_CONTEXT = DaoFactory.getTripDaoContext();
    private static final DaoFactory.DaoContext<RouteDao> ROUTE_CONTEXT = DaoFactory.getRouteDaoContext();
    private static final DaoFactory.DaoContext<BookingDao> BOOKING_CONTEXT = DaoFactory.getBookingDaoContext();
    private static final DaoFactory.DaoContext<RatingDao> RATING_CONTEXT = DaoFactory.getRatingDaoContext();

    // Сервисы с внедренными зависимостями
    private static final UserService USER_SERVICE = new UserServiceImpl(
            USER_CONTEXT.dao(),
            USER_CONTEXT.dataAccessManager()
    );

    private static final TripService TRIP_SERVICE = new TripServiceImpl(
            TRIP_CONTEXT.dao(),
            ROUTE_CONTEXT.dao(),
            USER_CONTEXT.dao(),
            TRIP_CONTEXT.dataAccessManager()
    );

    private static final BookingService BOOKING_SERVICE = new BookingServiceImpl(
            BOOKING_CONTEXT.dao(),
            TRIP_CONTEXT.dao(),
            USER_CONTEXT.dao(),
            BOOKING_CONTEXT.dataAccessManager()
    );

    private static final RatingService RATING_SERVICE = new RatingServiceImpl(
            RATING_CONTEXT.dao(),
            TRIP_CONTEXT.dao(),
            USER_CONTEXT.dao(),
            BOOKING_CONTEXT.dao(),
            RATING_CONTEXT.dataAccessManager()
    );

    public static UserService getUserService() { return USER_SERVICE; }
    public static TripService getTripService() { return TRIP_SERVICE; }
    public static BookingService getBookingService() { return BOOKING_SERVICE; }
    public static RatingService getRatingService() { return RATING_SERVICE; }

}