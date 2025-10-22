package repository;

import config.ApplicationProperties;
import entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;

import java.time.LocalDate;
import java.util.Properties;

import static config.ApplicationProperties.*;

public class Runner {
    private static final Properties PROPERTIES = new ApplicationProperties();

    static {
        PROPERTIES.setProperty(Environment.JAKARTA_JDBC_DRIVER, getApplicationProperty(DB_DRIVER));
        PROPERTIES.setProperty(Environment.JAKARTA_JDBC_URL, getApplicationProperty(DB_URL));
        PROPERTIES.setProperty(Environment.DIALECT, getApplicationProperty(DB_DIALECT));
        PROPERTIES.setProperty(Environment.JAKARTA_JDBC_USER, getApplicationProperty(DB_USER));
        PROPERTIES.setProperty(Environment.JAKARTA_JDBC_PASSWORD, getApplicationProperty(DB_PASSWORD));
    }

    private static final Repository REPOSITORY = new Repository(PROPERTIES);
    private static final SessionFactory SESSION_FACTORY = REPOSITORY.getSessionFactory();

    private static Staff staff;
    private static Inventory inventory;
    private static Language language;
    private static Film otherFilm;

    public static void main(String[] args) {


        getParametersForTest();

        String firstName = "Valera";
        String secondName = "Valerev";
        String email = "firstName@mail";
        String title = "Madagascar";
        String description = "it's good film";
        LocalDate releaseDate = LocalDate.parse("1999-12-22");

        REPOSITORY.addNewCustomer(firstName, secondName, email, staff.getStore(), staff.getAddress());
        System.out.printf("new customer %s, %s, %s added \n", firstName,secondName,email);

        REPOSITORY.addNewFilmToStore(title, description, releaseDate, language, (short) 10, 12.5f);
        System.out.printf("new film %s, %s added \n", title, description);

        Customer customer = REPOSITORY.getCustomerFromDb(email);
        Film film = REPOSITORY.getFilmFromDb(title);

        Payment payment = REPOSITORY.rentalInventory(customer, staff, inventory, 10.0f, film);
        System.out.printf("Customer %s is rent film %s\n", customer.getFirstName(), film.getTitle());

        Rental rental = payment.getRental();
        REPOSITORY.returnFilmToStore(rental);
        System.out.printf("Customer %s is return film %s\n", customer.getFirstName(), film.getTitle());

        rollbackMethodRentalInventory(payment);
        rollbackMethodAddNewFilmToStore(film);
        rollbackMethodAddNewCustomer(customer);
        System.out.print("Changes was rollback from bd");
    }

    private static void getParametersForTest() {
        Session session = SESSION_FACTORY.openSession();
        session.beginTransaction();
        try (session) {
            staff = session.find(Staff.class, 1);
            inventory = session.find(Inventory.class, 1);
            language = session.find(Language.class, 1);
            otherFilm = session.find(Film.class, 1);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
    }

    private static void rollbackMethodAddNewCustomer(Customer customer) {
        Session session = SESSION_FACTORY.openSession();
        session.beginTransaction();
        try (session) {
            session.remove(customer);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
    }

    private static void rollbackMethodAddNewFilmToStore(Film film) {
        Session session = SESSION_FACTORY.openSession();
        session.beginTransaction();
        try (session) {
            session.remove(film);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
    }

    private static void rollbackMethodRentalInventory(Payment payment) {
        Rental rental = payment.getRental();
        Inventory inventory = rental.getInventory();
        inventory.setFilm(otherFilm);
        Session session = SESSION_FACTORY.openSession();
        session.beginTransaction();
        try (session) {
            session.remove(payment);
            session.remove(rental);
            session.merge(inventory);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }
    }
}
