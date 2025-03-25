package com.carpooling.services.impl;

import com.carpooling.constants.ErrorMessages;
import com.carpooling.constants.LogMessages;
import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.RouteServiceException;
import com.carpooling.services.base.RouteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


import org.hibernate.Session;
import org.hibernate.query.Query;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteDao routeDao;

    @Override
    public String createRoute(@NotNull Route route) throws RouteServiceException {
        try {
            String routeId = routeDao.createRoute(route);
            log.info("Route created successfully: {}", routeId);
            return routeId;
        } catch (DataAccessException e) {
            log.error("Error creating route: {}", e.getMessage());
            throw new RouteServiceException("Error creating route", e);
        }
    }

    @Override
    public Optional<Route> getRouteById(String routeId) throws RouteServiceException {
        try {
            Optional<Route> routeOptional = routeDao.getRouteById(routeId);
            if (routeOptional.isPresent()) {
                log.info("Route found: {}", routeId);
            } else {
                log.warn("Route not found: {}", routeId);
            }
            return routeOptional;
        } catch (DataAccessException e) {
            log.error("Error reading route: {}", e.getMessage());
            throw new RouteServiceException("Error reading route", e);
        }
    }

    @Override
    public List<Route> getAllRoutes() throws RouteServiceException {
        try {
            List<Route> routes = Collections.emptyList();
            log.info("Retrieved {} routes successfully", routes.size());
            return routes;
        } catch (Exception e) {
            log.error("Error retrieving all routes: {}", e.getMessage());
            throw new RouteServiceException("Error retrieving all routes", e);
        }
    }

    @Override
    public void updateRoute(@NotNull Route route) throws RouteServiceException {
        try {
            routeDao.updateRoute(route);
            log.info("Route updated successfully: {}", route.getId());
        } catch (DataAccessException e) {
            log.error("Error updating route: {}", e.getMessage());
            throw new RouteServiceException("Error updating route", e);
        }
    }

    @Override
    public void deleteRoute(String routeId) throws RouteServiceException {
        try {
            routeDao.deleteRoute(routeId);
            log.info("Route deleted successfully: {}", routeId);
        } catch (DataAccessException e) {
            log.error("Error deleting route: {}", e.getMessage());
            throw new RouteServiceException("Error deleting route", e);
        }
    }

    @Override
    public List<Route> findRoutesByStartPoint(String startPoint) throws RouteServiceException {
        try {
            List<Route> routes = Collections.emptyList();
            log.info("Found {} routes by start point: {}", routes.size(), startPoint);
            return routes;
        } catch (Exception e) {
            log.error("Error finding routes by start point: {}", e.getMessage());
            throw new RouteServiceException("Error finding routes by start point", e);
        }
    }

    @Override
    public List<Route> findRoutesByEndPoint(String endPoint) throws RouteServiceException {
        try {
            List<Route> routes = Collections.emptyList();
            log.info("Found {} routes by end point: {}", routes.size(), endPoint);
            return routes;
        } catch (Exception e) {
            log.error("Error finding routes by end point: {}", e.getMessage());
            throw new RouteServiceException("Error finding routes by end point", e);
        }
    }

    @Override
    public List<Route> findRoutesByStartAndEndPoints(String startPoint, String endPoint) throws RouteServiceException {
        try {
            List<Route> routes = Collections.emptyList();
            log.info("Found {} routes by start point: {} and end point: {}", routes.size(), startPoint, endPoint);
            return routes;
        } catch (Exception e) {
            log.error("Error finding routes by start and end points: {}", e.getMessage());
            throw new RouteServiceException("Error finding routes by start and end points", e);
        }
    }

    @Override
    public List<Route> getRoutesByUser(String userId) throws RouteServiceException {
        try  {
            List<Route> routes = Collections.emptyList();
            log.info("Found {} routes for user: {}", routes.size(), userId);
            return routes;
        } catch (Exception e) {
            log.error("Error retrieving routes for user: {}", e.getMessage());
            throw new RouteServiceException("Error retrieving routes for user", e);
        }
    }

    @Override
    public List<Route> getRoutesByDate(String date) throws RouteServiceException {
        try {
            List<Route> routes = Collections.emptyList();
            log.info("Found {} routes for date: {}", routes.size(), date);
            return routes;
        } catch (Exception e) {
            log.error("Error retrieving routes by date: {}", e.getMessage());
            throw new RouteServiceException("Error retrieving routes by date", e);
        }
    }
}