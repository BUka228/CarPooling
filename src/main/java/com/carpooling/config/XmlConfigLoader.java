package com.carpooling.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.carpooling.constants.ErrorMessages.ERROR_LOADING_XML;
import static com.carpooling.constants.LogMessages.FAILED_TO_LOAD_XML;

public class XmlConfigLoader extends ConfigLoader {

    @Override
    public Properties load(File file) throws IOException {
        Properties props = new Properties();
        try (InputStream input = createFileInputStream(file)) {
            props.loadFromXML(input);
        } catch (IOException e) {
            log.error(FAILED_TO_LOAD_XML, file.getAbsolutePath(), e);
            throw new IOException(ERROR_LOADING_XML + file, e);
        }
        return props;
    }
}
