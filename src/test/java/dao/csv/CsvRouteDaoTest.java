package dao.csv;

import com.carpooling.dao.csv.CsvRouteDao;
import com.carpooling.entities.record.RouteRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CsvRouteDaoTest {

    @TempDir
    Path tempDir; // Временная директория для тестов

    private CsvRouteDao routeDao;

    @BeforeEach
    void setUp() {
        // Создаем временный файл для тестов
        File tempFile = tempDir.resolve("test-routes.csv").toFile();
        routeDao = new CsvRouteDao(tempFile.getAbsolutePath());
    }

    @Test
    void testCreateRoute_Success() {
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Point A");
        routeRecord.setEndPoint("Point B");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 120);

        String routeId = routeDao.createRoute(routeRecord);

        // Проверяем, что ID был сгенерирован и соответствует формату UUID
        assertNotNull(routeId);
        assertDoesNotThrow(() -> UUID.fromString(routeId));

        // Проверяем, что маршрут был добавлен
        Optional<RouteRecord> foundRoute = routeDao.getRouteById(routeId);
        assertTrue(foundRoute.isPresent());
        assertEquals("Point A", foundRoute.get().getStartPoint());
    }

    @Test
    void testCreateRoute_Fail() {
        // Создаем временный файл и делаем его недоступным для записи
        File tempFile = tempDir.resolve("test-routes.csv").toFile();
        tempFile.setReadOnly();

        // Пытаемся создать маршрут, ожидая исключение
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Point X");
        routeRecord.setEndPoint("Point Y");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 30);

        assertThrows(DataAccessException.class, () -> routeDao.createRoute(routeRecord));

        // Восстанавливаем права доступа к файлу
        tempFile.setWritable(true);
    }


    @Test
    void testGetRouteById_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Point C");
        routeRecord.setEndPoint("Point D");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 90);

        String routeId = routeDao.createRoute(routeRecord);

        // Получаем маршрут по ID
        Optional<RouteRecord> foundRoute = routeDao.getRouteById(routeId);

        // Проверяем, что маршрут найден
        assertTrue(foundRoute.isPresent());
        assertEquals(routeId, foundRoute.get().getId());
        assertEquals("Point C", foundRoute.get().getStartPoint());
    }

    @Test
    void testGetRouteById_NotFound() {
        // Пытаемся получить несуществующий маршрут
        Optional<RouteRecord> foundRoute = routeDao.getRouteById("non-existent-id");

        // Проверяем, что маршрут не найден
        assertFalse(foundRoute.isPresent());
    }

    @Test
    void testUpdateRoute_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Point E");
        routeRecord.setEndPoint("Point F");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 60);

        String routeId = routeDao.createRoute(routeRecord);

        // Обновляем маршрут
        routeRecord.setStartPoint("Point G");
        routeDao.updateRoute(routeRecord);

        // Проверяем, что маршрут обновлен
        Optional<RouteRecord> updatedRoute = routeDao.getRouteById(routeId);
        assertTrue(updatedRoute.isPresent());
        assertEquals("Point G", updatedRoute.get().getStartPoint());
    }

    @Test
    void testUpdateRoute_NotFound() {
        // Пытаемся обновить несуществующий маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setId("non-existent-id");
        routeRecord.setStartPoint("Point H");

        assertThrows(DataAccessException.class, () -> routeDao.updateRoute(routeRecord));
    }

    @Test
    void testDeleteRoute_Success() {
        // Создаем тестовый маршрут
        RouteRecord routeRecord = new RouteRecord();
        routeRecord.setStartPoint("Point I");
        routeRecord.setEndPoint("Point J");
        routeRecord.setDate(new Date());
        routeRecord.setEstimatedDuration((short) 45);

        String routeId = routeDao.createRoute(routeRecord);

        // Удаляем маршрут
        routeDao.deleteRoute(routeId);

        // Проверяем, что маршрут удален
        Optional<RouteRecord> deletedRoute = routeDao.getRouteById(routeId);
        assertFalse(deletedRoute.isPresent());
    }

    @Test
    void testDeleteRoute_NotFound() {
        // Пытаемся удалить несуществующий маршрут
        assertThrows(DataAccessException.class, () -> routeDao.deleteRoute("non-existent-id"));
    }
}