import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarPoolingClientTest {
    private static final Logger log = LoggerFactory.getLogger(CarPoolingClientTest.class);

    @Test
    public void testLogBasicSystemInfo() {
        Logger log = LoggerFactory.getLogger(CarPoolingClientTest.class);
        log.info("Launching the application...");
        log.info("Operating System: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
        log.info("JRE: {}", System.getProperty("java.version"));
        log.info("Java Launched From: {}", System.getProperty("java.home"));
        log.info("Class Path: {}", System.getProperty("java.class.path"));
        log.info("Library Path: {}", System.getProperty("java.library.path"));
        log.info("User Home Directory: {}", System.getProperty("user.home"));
        log.info("User Working Directory: {}", System.getProperty("user.dir"));
        log.info("Test INFO logging.");
    }

}
