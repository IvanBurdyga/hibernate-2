package config;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    public static final String DB_DRIVER = "db.driver";
    public static final String DB_URL = "db.uri";
    public static final String DB_USER = "db.user";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_DIALECT = "db.dialect";

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ApplicationProperties.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getApplicationProperty(String key) {
        return properties.getProperty(key);
    }
}
