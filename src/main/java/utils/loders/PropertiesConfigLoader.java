package utils.loders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.man.constant.ErrorMessages.ERROR_LOADING_PROPERTIES;
import static com.man.constant.LogMessages.FAILED_TO_LOAD_PROPERTIES;

public class PropertiesConfigLoader extends ConfigLoader {

    public Properties load(InputStream input) throws IOException {
        Properties props = new Properties();
        try (input) {
            props.load(input);
        } catch (IOException e) {
            log.error(FAILED_TO_LOAD_PROPERTIES, "input stream", e);
            throw new IOException(ERROR_LOADING_PROPERTIES + "input stream", e);
        }
        return props;
    }

    // Перегруженный метод для обратной совместимости с File
    @Override
    public Properties load(File file) throws IOException {
        try (InputStream input = createFileInputStream(file)) {
            return load(input);
        }
    }
}
