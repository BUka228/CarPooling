package dao.xml;


import com.carpooling.dao.xml.XmlTripDao;
import com.carpooling.entities.database.Trip;
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

class XmlTripDaoTest {

    private XmlTripDao tripDao;
    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() {
        String testFileName = "test-trips.xml";
        tempFile = tempDir.resolve(testFileName).toFile();
        tripDao = new XmlTripDao(tempFile.getAbsolutePath());
    }

    private Trip createTestTrip() {
        Trip trip = new Trip();
        trip.setDepartureTime(new Date(System.currentTimeMillis() + 1000L * 3600 * 24)); // +1 day
        trip.setMaxPassengers((byte) 4);
        trip.setCreationDate(new Date());
        trip.setStatus("scheduled");
        trip.setEditable(true);
        // trip.setUser(new User()); // Assuming User exists, though transient
        // trip.setRoute(new Route()); // Assuming Route exists, though transient
        // Bookings and Ratings are transient Sets
        return trip;
    }

    @Test
    void createTrip_Success() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);

        assertNotNull(id);
        UUID generatedUUID = assertDoesNotThrow(() -> UUID.fromString(id));

        Optional<Trip> foundTripOpt = tripDao.getTripById(id);
        assertTrue(foundTripOpt.isPresent(), "Trip should be found after creation");
        Trip foundTrip = foundTripOpt.get();

        assertEquals(generatedUUID, foundTrip.getId());
        assertEquals(trip.getMaxPassengers(), foundTrip.getMaxPassengers());
        assertEquals(trip.getStatus(), foundTrip.getStatus());
        assertEquals(trip.isEditable(), foundTrip.isEditable());
        assertNotNull(foundTrip.getDepartureTime());
        assertNotNull(foundTrip.getCreationDate());
    }

    @Test
    void createTrip_DataAccessException_OnFileError() {
        Trip trip = createTestTrip();
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> tripDao.createTrip(trip));
        tempFile.setWritable(true);
    }

    @Test
    void createTrip_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> tripDao.createTrip(null));
    }


    @Test
    void getTripById_Success() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);
        Optional<Trip> foundTrip = tripDao.getTripById(id);
        assertTrue(foundTrip.isPresent());
        assertEquals(UUID.fromString(id), foundTrip.get().getId());
        assertEquals(trip.getStatus(), foundTrip.get().getStatus());
    }

    @Test
    void getTripById_NotFound() throws DataAccessException {
        String nonExistentId = UUID.randomUUID().toString();
        Optional<Trip> foundTrip = tripDao.getTripById(nonExistentId);
        assertFalse(foundTrip.isPresent());
    }

    @Test
    void getTripById_DataAccessException_OnFileError() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);
        assertTrue(tempFile.delete());
        assertThrows(IllegalArgumentException.class, () -> tripDao.getTripById(id));
    }

    @Test
    void updateTrip_Success() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);
        UUID tripUUID = UUID.fromString(id);

        Trip createdTrip = tripDao.getTripById(id).orElseThrow(() -> new AssertionError("Failed to retrieve trip for update test"));

        createdTrip.setStatus("completed");
        createdTrip.setEditable(false);
        tripDao.updateTrip(createdTrip);

        Optional<Trip> updatedTripOpt = tripDao.getTripById(id);
        assertTrue(updatedTripOpt.isPresent());
        Trip updatedTrip = updatedTripOpt.get();

        assertEquals("completed", updatedTrip.getStatus());
        assertFalse(updatedTrip.isEditable());
        assertEquals(tripUUID, updatedTrip.getId());
    }

    @Test
    void updateTrip_NotFound() {
        Trip nonExistentTrip = createTestTrip();
        nonExistentTrip.setId(UUID.randomUUID());
        assertThrows(DataAccessException.class, () -> tripDao.updateTrip(nonExistentTrip));
    }

    @Test
    void updateTrip_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> tripDao.updateTrip(null));
    }

    @Test
    void deleteTrip_Success() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);
        assertTrue(tripDao.getTripById(id).isPresent());
        tripDao.deleteTrip(id);
        assertFalse(tripDao.getTripById(id).isPresent());
    }

    @Test
    void deleteTrip_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> tripDao.deleteTrip(nonExistentId));
    }

    @Test
    void deleteTrip_DataAccessException_OnFileError() throws DataAccessException {
        Trip trip = createTestTrip();
        String id = tripDao.createTrip(trip);
        assertTrue(tempFile.setWritable(false));
        assertThrows(DataAccessException.class, () -> tripDao.deleteTrip(id));
        tempFile.setWritable(true);
    }
}
