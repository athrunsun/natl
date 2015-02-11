package net.nitrogen.ates.util;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties load(String filePath) {
        Properties properties = new Properties();
        ByteSource supplier = Resources.asByteSource(Resources.getResource(filePath));
        InputStream inputStream;

        try {
            inputStream = supplier.openStream();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Exception when opening input stream for file path:%properties!", filePath));
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Exception when loading properties from file!");
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
