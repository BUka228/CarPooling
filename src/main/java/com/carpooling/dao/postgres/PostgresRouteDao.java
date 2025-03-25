package com.carpooling.dao.postgres;


import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.utils.HibernateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.HibernateException;


@Slf4j
@AllArgsConstructor
public class PostgresRouteDao implements RouteDao {

    private final SessionFactory sessionFactory;

    @Override
    public String createRoute(Route route) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(route);
            transaction.commit();
            log.info("Route created successfully: {}", route.getId());
            return route.getId().toString();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error creating route: {}", e.getMessage());
            throw new DataAccessException("Error creating route", e);
        }
    }

    @Override
    public Optional<Route> getRouteById(String id) throws DataAccessException {
        try (Session session = sessionFactory.openSession()) {
            UUID uuid = UUID.fromString(id);
            Route route = session.get(Route.class, uuid);
            if (route != null) {
                log.info("Route found: {}", id);
            } else {
                log.warn("Route not found: {}", id);
            }
            return Optional.ofNullable(route);
        } catch (HibernateException e) {
            log.error("Error reading route: {}", e.getMessage());
            throw new DataAccessException("Error reading route", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }

    @Override
    public void updateRoute(Route route) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(route);
            transaction.commit();
            log.info("Route updated successfully: {}", route.getId());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error updating route: {}", e.getMessage());
            throw new DataAccessException("Error updating route", e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UUID uuid = UUID.fromString(id);
            Route route = session.get(Route.class, uuid);
            if (route != null) {
                session.remove(route);
                transaction.commit();
                log.info("Route deleted successfully: {}", id);
            } else {
                log.warn("Route not found for deletion: {}", id);
            }
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Error deleting route: {}", e.getMessage());
            throw new DataAccessException("Error deleting route", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", id);
            throw new DataAccessException("Invalid UUID format", e);
        }
    }
}