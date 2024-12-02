package utils.loders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class YamlConfigLoader extends ConfigLoader {
    @Override
    public Properties load(File file) throws IOException {
        Properties props = new Properties();
        try (FileInputStream input = createFileInputStream(file)) {
            props.load(input);
        } catch (IOException e) {
            log.error("Failed to load properties from {}", file.getAbsolutePath(), e);
            throw new IOException("Error loading configuration file: " + file, e);
        }
        return props;
    }
}
