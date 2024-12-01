package utils.loders;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public interface ConfigLoader {
    Properties load(File file) throws IOException;
}
