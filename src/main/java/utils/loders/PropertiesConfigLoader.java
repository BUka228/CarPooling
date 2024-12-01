package utils.loders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfigLoader implements ConfigLoader {
    @Override
    public Properties load(File file) throws IOException {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(file)) {
            props.load(input);
        }
        return props;
    }
}
