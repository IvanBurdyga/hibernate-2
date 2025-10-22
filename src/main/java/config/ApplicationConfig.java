package config;

import entity.*;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class ApplicationConfig  extends Configuration {
    private static final Configuration configuration = new Configuration();

    static {
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Address.class);
        configuration.addAnnotatedClass(Category.class);
        configuration.addAnnotatedClass(City.class);
        configuration.addAnnotatedClass(Country.class);
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Film.class);
        configuration.addAnnotatedClass(FilmText.class);
        configuration.addAnnotatedClass(Inventory.class);
        configuration.addAnnotatedClass(Language.class);
        configuration.addAnnotatedClass(Payment.class);
        configuration.addAnnotatedClass(Rental.class);
        configuration.addAnnotatedClass(Staff.class);
        configuration.addAnnotatedClass(Store.class);
    }

    public static Configuration getApplicationConfig(Properties properties) {

        configuration.setProperties(properties);
        return configuration;
    }
}
