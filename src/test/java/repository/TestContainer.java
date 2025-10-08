package repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.Properties;

public class TestContainer {

    private final static JdbcDatabaseContainer<?> CONTAINER;
    private final static String MYSQL_IMAGE_NAME = "mysql:8.0";
    public final static SessionFactory sessionFactory;

    static {
        CONTAINER = new MySQLContainer<>(MYSQL_IMAGE_NAME);
        CONTAINER.start();

        Properties properties = new Properties();
        properties.put(Environment.JAKARTA_JDBC_DRIVER,CONTAINER.getDriverClassName());
        properties.put(Environment.JAKARTA_JDBC_URL, CONTAINER.getJdbcUrl());
        properties.put(Environment.JAKARTA_JDBC_USER, CONTAINER.getUsername());
        properties.put(Environment.JAKARTA_JDBC_PASSWORD, CONTAINER.getPassword());

        sessionFactory = new Configuration()
                .setProperties(properties)
                .buildSessionFactory();
    }
}
