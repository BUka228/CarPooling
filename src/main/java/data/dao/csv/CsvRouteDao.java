package data.dao.csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import data.dao.base.RouteDao;
import data.model.record.RouteRecord;
import exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.man.constant.ErrorMessages.ROUTE_CREATION_ERROR;
import static com.man.constant.ErrorMessages.ROUTE_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;

@Slf4j
public class CsvRouteDao extends AbstractCsvDao<RouteRecord> implements RouteDao {

    public CsvRouteDao(String file) {
        super(RouteRecord.class, file);
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
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_CREATE_ROUTE, routeRecord.getStartPoint(), routeRecord.getEndPoint(), e);
            throw new DataAccessException(ROUTE_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RouteRecord> getRouteById(String id) throws DataAccessException {
        log.info(GET_ROUTE_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (IOException e) {
            log.error(ERROR_GET_ROUTE, id, e);
            throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRoute(@NotNull RouteRecord routeRecord) throws DataAccessException {
        try {
            List<RouteRecord> routes = readAll();
            boolean found = false;
            for (int i = 0; i < routes.size(); i++) {
                if (routes.get(i).getId().equals(routeRecord.getId())) {
                    routes.set(i, routeRecord);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.warn(WARN_ROUTE_NOT_FOUND, routeRecord.getId());
                throw new DataAccessException(String.format(ROUTE_NOT_FOUND_ERROR, routeRecord.getId()));
            }
            writeAll(routes);
            log.info(UPDATE_ROUTE_SUCCESS, routeRecord.getId());
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_UPDATE_ROUTE, routeRecord.getId(), e);
            throw new DataAccessException(ROUTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRoute(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_ROUTE_SUCCESS, id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error(ERROR_DELETE_ROUTE, id, e);
            throw new DataAccessException(ROUTE_DELETE_ERROR, e);
        }
    }
}