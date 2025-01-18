package com.carpooling.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public abstract class ConfigLoader {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    public abstract Properties load(File file) throws IOException;
    protected FileInputStream createFileInputStream(File file) throws IOException {
        return new FileInputStream(file);
    }
}