package com.carpooling.dao.postgres;

import com.carpooling.dao.base.TripDao;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.dao.DataAccessException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.carpooling.constants.Constants.*;

@Slf4j
public class PostgresTripDao extends AbstractPostgresDao<Trip, UUID> implements TripDao {

    public PostgresTripDao(SessionFactory sessionFactory) {
        super(sessionFactory, Trip.class);
    }

    @Override
    public String createTrip(Trip trip) throws DataAccessException {
        // Убеждаемся, что связанные Route и User уже persistent или настроен каскад
        persistEntity(trip);
        if (trip.getId() == null) {
            throw new DataAccessException("Failed to generate ID for trip");
        }
        return trip.getId().toString();
    }

    @Override
    public Optional<Trip> getTripById(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "trip id");
        // Используем HQL для загрузки связей
        log.debug("Looking up Trip by id {} with details", id);
        try {
            Query<Trip> query = getCurrentSession().createQuery(GET_TRIP_BY_ID_WITH_DETAILS_HQL, Trip.class);
            query.setParameter("tripId", uuid);
            return query.uniqueResultOptional();
        } catch (PersistenceException e) {
            log.error("Error reading Trip by id {}: {}", id, e.getMessage());
            throw new DataAccessException("Error reading Trip", e);
        }
    }

    @Override
    public void updateTrip(Trip trip) throws DataAccessException {
        mergeEntity(trip);
    }

    @Override
    public void deleteTrip(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "trip id");
        deleteEntityById(uuid);
    }

    @Override
    public List<Trip> findTrips(String startPoint, String endPoint, LocalDate date) throws DataAccessException {
        log.debug("Finding trips with criteria: start={}, end={}, date={}", startPoint, endPoint, date);
        StringBuilder hqlBuilder = new StringBuilder(FIND_TRIPS_HQL_BASE);
        Map<String, Object> parameters = new HashMap<>();

        if (startPoint != null && !startPoint.trim().isEmpty()) {
            hqlBuilder.append(FIND_TRIPS_HQL_START_POINT);
            parameters.put("startPoint", "%" + startPoint.trim() + "%");
        }
        if (endPoint != null && !endPoint.trim().isEmpty()) {
            hqlBuilder.append(FIND_TRIPS_HQL_END_POINT);
            parameters.put("endPoint", "%" + endPoint.trim() + "%");
        }
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            hqlBuilder.append(FIND_TRIPS_HQL_DATE_RANGE);
            parameters.put("startDate", startOfDay);
            parameters.put("endDate", endOfDay);
        }
        hqlBuilder.append(FIND_TRIPS_HQL_ORDER_BY);

        String hql = hqlBuilder.toString();
        log.trace("Executing HQL for findTrips: {}", hql);
        log.trace("Parameters: {}", parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", ")));

        try {
            Session session = getCurrentSession();
            Query<Trip> query = session.createQuery(hql, Trip.class);
            parameters.forEach(query::setParameter);
            return query.list();
        } catch (PersistenceException e) {
            log.error("Error finding trips with criteria: {}", e.getMessage());
            throw new DataAccessException("Error finding trips", e);
        }
    }
}
