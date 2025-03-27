package com.carpooling.dao.postgres;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PostgresRouteDao extends AbstractPostgresDao<Route, UUID> implements RouteDao {

    public PostgresRouteDao(SessionFactory sessionFactory) {
        super(sessionFactory, Route.class);
    }

    @Override
    public String createRoute(Route route) throws DataAccessException {
        persistEntity(route);
        if (route.getId() == null) {
            throw new DataAccessException("Failed to generate ID for route");
        }
        return route.getId().toString();
    }

    @Override
    public Optional<Route> getRouteById(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "route id");
        return findEntityById(uuid);
    }

    @Override
    public void updateRoute(Route route) throws DataAccessException {
        mergeEntity(route);
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        UUID uuid = parseUUID(id, "route id");
        deleteEntityById(uuid);
    }
}