package com.flash.service.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PropertyReader {
    public Set<String> readAllPropertyNames(String propsFilePath) {
        try (FileInputStream fileInputStream = new FileInputStream(propsFilePath)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties.stringPropertyNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    public Properties readProperty(String filePath) throws IOException {
        try (
                FileInputStream fileInputStream = new FileInputStream(filePath)
        ) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        }
    }
}
