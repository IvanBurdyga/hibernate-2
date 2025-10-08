package repository;

import config.Config;
import entity.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

import static config.Config.*;

public class Repository {

    private final SessionFactory sessionFactory;

    public Repository() {
        Properties properties = new Properties();
        properties.put(Environment.JAKARTA_JDBC_DRIVER, Config.getProperties(DB_DRIVER));
        properties.put(Environment.JAKARTA_JDBC_URL, Config.getProperties(DB_URL));
        properties.put(Environment.DIALECT, Config.getProperties(DB_DIALECT));
        properties.put(Environment.JAKARTA_JDBC_USER, Config.getProperties(DB_USER));
        properties.put(Environment.JAKARTA_JDBC_PASSWORD, Config.getProperties(DB_PASSWORD));
        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .buildSessionFactory();
    }

    @Transactional
    public Customer addNewCustomer(String firstName, String lastName, String email, Store store, Address address) {
        Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .store(store)
                .address(address)
                .build();

        Session session = sessionFactory.openSession();
        session.persist(customer);
        return customer;
    }

    @Transactional
    public void returnFilmToStore(Rental rental) {
        rental.setReturnDate(LocalDateTime.now());
        Session session = sessionFactory.openSession();
            session.merge(rental);
    }

    @Transactional
    public void rentalInventory(Customer customer, Staff staff, Inventory inventory, Float amount, Film film) {
        Rental rental = Rental.builder()
                .rentalDate(LocalDateTime.now())
                .inventory(inventory)
                .customer(customer)
                .staff(staff)
                .build();

        Payment payment = Payment.builder()
                .customer(customer)
                .staff(staff)
                .rental(rental)
                .paymentDate(LocalDateTime.now())
                .amount(amount)
                .build();

        inventory.setFilm(film);

        Session session = sessionFactory.openSession();
            session.persist(payment);
            session.persist(rental);
            session.merge(inventory);
    }

    @Transactional
    public void addNewFilmToStore(String title, String description, LocalDate releaseDate, Language language, Short length, Float price) {
        Film film = Film.builder()
                .title(title)
                .description(description)
                .releaseYear(releaseDate)
                .language(language)
                .rentalDuration((short) 0)
                .rentalRate((float) 0)
                .length(length)
                .replacementCost(price).build();
        Session session = sessionFactory.openSession();
        session.merge(film);
    }
}
