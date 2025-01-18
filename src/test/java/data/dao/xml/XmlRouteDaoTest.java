package data.dao.xml;

import data.model.database.Route;
import data.model.record.RouteRecord;
import exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlRouteDaoTest {

    private XmlRouteDao routeDao;
    @TempDir
    Path tempDir; // Временная директория для тестов

    private File tempFile;



    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        tempFile = tempDir.resolve("test-routes.xml").toFile();
        routeDao = new XmlRouteDao(tempFile.getAbsolutePath());
    }


    @Test
    void testCreateRoute_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        // Создаем маршрут
        String routeId = routeDao.createRoute(routeRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(routeId);
        assertDoesNotThrow(() -> UUID.fromString(routeId));

        // Проверяем, что маршрут был добавлен
        Optional<RouteRecord> foundRoute = routeDao.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals("Start Point", foundRoute.get().getStartPoint());
    }

    @Test
    void testCreateRoute_Failure() {
        // Создаем тестовый маршрут с некорректными данными (например, null)
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint(null); // Некорректные данные
        tempFile.setReadOnly();


        // Проверяем, что создание маршрута выбрасывает исключение
        assertThrows(DataAccessException.class, () -> routeDao.createRoute(routeRecord));
    }

    @Test
    void testGetRouteById_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        // Создаем маршрут и получаем его ID
        String routeId = routeDao.createRoute(routeRecord);

        // Получаем маршрут по ID
        Optional<RouteRecord> foundRoute = routeDao.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals(routeId, foundRoute.get().getId());
    }

    @Test
    void testGetRouteById_NotFound() {
        // Пытаемся получить несуществующий маршрут
        Optional<RouteRecord> foundRoute = routeDao.getRouteById("non-existent-id");
        assertFalse(foundRoute.isPresent());
    }

    @Test
    void testUpdateRoute_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        // Создаем маршрут и получаем его ID
        String routeId = routeDao.createRoute(routeRecord);

        // Обновляем маршрут
        routeRecord.setEndPoint("Updated End Point");
        routeDao.updateRoute(routeRecord);

        // Проверяем, что маршрут был обновлен
        Optional<RouteRecord> updatedRoute = routeDao.getRouteById(routeId);
        assertTrue(updatedRoute.isPresent());
        assertEquals("Updated End Point", updatedRoute.get().getEndPoint());
    }

    @Test
    void testUpdateRoute_NotFound() {
        // Пытаемся обновить несуществующий маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setId("non-existent-id");
        routeRecord.setStartPoint("Start Point");

        // Проверяем, что обновление выбрасывает исключение
        assertThrows(DataAccessException.class, () -> routeDao.updateRoute(routeRecord));
    }

    @Test
    void testDeleteRoute_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Start Point");
        routeRecord.setEndPoint("End Point");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        // Создаем маршрут и получаем его ID
        String routeId = routeDao.createRoute(routeRecord);

        // Удаляем маршрут
        routeDao.deleteRoute(routeId);

        // Проверяем, что маршрут был удален
        Optional<RouteRecord> deletedRoute = routeDao.getRouteById(routeId);
        assertFalse(deletedRoute.isPresent());
    }

    @Test
    void testDeleteRoute_NotFound() {
        // Пытаемся удалить несуществующий маршрут
        assertThrows(DataAccessException.class, () -> routeDao.deleteRoute("non-existent-id"));
    }
}