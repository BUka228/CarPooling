package com.carpooling.dao.xml;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.database.Route;
import com.carpooling.exceptions.dao.DataAccessException;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class XmlRouteDao extends AbstractXmlDao<Route, XmlRouteDao.RouteWrapper> implements RouteDao {

    public XmlRouteDao(String filePath) {
        super(Route.class, RouteWrapper.class, filePath);
    }

    @Override
    public String createRoute(@NotNull Route route) throws DataAccessException {
        UUID routeId = generateId();
        route.setId(routeId);

        try {
            List<Route> routes = readAll();
            routes.add(route);
            writeAll(routes);
            log.info("Route created successfully: {}", routeId);
            return routeId.toString();
        } catch (JAXBException e) {
            log.error("Error creating route: {}", e.getMessage());
            throw new DataAccessException("Error creating route", e);
        }
    }

    @Override
    public Optional<Route> getRouteById(String id) throws DataAccessException {
        try {
            Optional<Route> route = findById(record -> record.getId().toString().equals(id));
            if (route.isPresent()) {
                log.info("Route found: {}", id);
            } else {
                log.warn("Route not found: {}", id);
            }
            return route;
        } catch (JAXBException e) {
            log.error("Error reading route: {}", e.getMessage());
            throw new DataAccessException("Error reading route", e);
        }
    }

    @Override
    public void updateRoute(@NotNull Route route) throws DataAccessException {
        try {
            boolean updated = updateItem(record -> record.getId().equals(route.getId()), route);
            if (!updated) {
                log.warn("Route not found for update: {}", route.getId());
                throw new DataAccessException("Route not found");
            }
            log.info("Route updated successfully: {}", route.getId());
        } catch (JAXBException e) {
            log.error("Error updating route: {}", e.getMessage());
            throw new DataAccessException("Error updating route", e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("Route deleted successfully: {}", id);
            } else {
                log.warn("Route not found for deletion: {}", id);
            }
        } catch (JAXBException e) {
            log.error("Error deleting route: {}", e.getMessage());
            throw new DataAccessException("Error deleting route", e);
        }
    }

    @Override
    protected List<Route> getItemsFromWrapper(@NotNull RouteWrapper wrapper) {
        return wrapper.getRoutes();
    }

    @Override
    protected RouteWrapper createWrapper(List<Route> items) {
        return new RouteWrapper(items);
    }

    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "routes")
    protected static class RouteWrapper {
        private List<Route> routes;

        @XmlElement(name = "route")
        public List<Route> getRoutes() {
            return routes;
        }
    }
}