package com.carpooling.dao.xml;

import com.carpooling.dao.base.RouteDao;
import com.carpooling.entities.record.RouteRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.ROUTE_CREATION_ERROR;
import static com.carpooling.constants.ErrorMessages.ROUTE_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;

@Slf4j
public class XmlRouteDao extends AbstractXmlDao<RouteRecord, XmlRouteDao.RouteWrapper> implements RouteDao {

    public XmlRouteDao(String filePath) {
        super(RouteRecord.class, RouteWrapper.class, filePath);
    }

    @Override
    public String createRoute(@NotNull RouteRecord routeRecord) throws DataAccessException {
        log.info(CREATE_ROUTE_START, routeRecord.getStartPoint(), routeRecord.getEndPoint());

        // Генерация UUID для ID
        String routeId = generateId();
        routeRecord.setId(routeId);

        try {
            List<RouteRecord> routes = readAll();
            routes.add(routeRecord);
            writeAll(routes);

            log.info(CREATE_ROUTE_SUCCESS, routeId);
            return routeId;
        } catch (JAXBException e) {
            log.error(ERROR_CREATE_ROUTE, routeRecord.getStartPoint(), routeRecord.getEndPoint(), e);
            throw new DataAccessException(ROUTE_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RouteRecord> getRouteById(String id) throws DataAccessException {
        log.info(GET_ROUTE_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (JAXBException e) {
            log.error(ERROR_GET_ROUTE, id, e);
            throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRoute(RouteRecord routeRecord) throws DataAccessException {
        try {
            List<RouteRecord> routes = readAll();
            boolean updated = updateItem(routes, record -> record.getId().equals(routeRecord.getId()), routeRecord);

            if (!updated) {
                log.warn(WARN_ROUTE_NOT_FOUND, routeRecord.getId());
                throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, routeRecord.getId()));
            }

            writeAll(routes);
            log.info(UPDATE_ROUTE_SUCCESS, routeRecord.getId());
        } catch (JAXBException e) {
            log.error(ERROR_UPDATE_ROUTE, routeRecord.getId(), e);
            throw new DataAccessException(ROUTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_ROUTE_SUCCESS, id);
        } catch (JAXBException e) {
            log.error(ERROR_DELETE_ROUTE, id, e);
            throw new DataAccessException(ROUTE_DELETE_ERROR, e);
        }
    }

    @Override
    protected List<RouteRecord> getItemsFromWrapper(@NotNull RouteWrapper wrapper) {
        return wrapper.getRoutes();
    }

    @Override
    protected RouteWrapper createWrapper(List<RouteRecord> items) {
        return new RouteWrapper(items);
    }

    /**
     * Вспомогательный класс для обертки списка маршрутов в XML.
     */
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "routes")
    protected static class RouteWrapper {
        private List<RouteRecord> routes;

        @XmlElement(name = "route")
        public List<RouteRecord> getRoutes() {
            return routes;
        }
    }
}
