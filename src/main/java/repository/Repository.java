package repository;

import config.ApplicationConfig;
import entity.*;
import exception.NoSuchCustomerFound;
import exception.NoSuchFilmFound;
import exception.NoSuchPaymentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

@AllArgsConstructor
@Builder
public class Repository {

    @Getter
    private final SessionFactory sessionFactory;

    @SneakyThrows
    public Repository(Properties properties) {
        sessionFactory = ApplicationConfig.getApplicationConfig(properties).buildSessionFactory();
    }

    public void addNewCustomer(String firstName, String lastName, String email, Store store, Address address) {
        Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .store(store)
                .address(address)
                .active((byte) 1)
                .build();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.persist(customer);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }

    }

    public void returnFilmToStore(Rental rental) {
        LocalDateTime date = LocalDateTime.now();
        rental.setReturnDate(date);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.merge(rental);
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    public Payment rentalInventory(Customer customer, Staff staff, Inventory inventory, Float amount, Film film) {

        LocalDateTime dateTime = LocalDateTime.now();
        Rental rental = getRental(customer, staff, inventory);
        Payment payment = getPayment(customer, staff, amount, rental, dateTime);
        inventory.setFilm(film);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.persist(payment);
            session.persist(rental);
            session.merge(inventory);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }

        return getLastUserPaymentFromDb(customer);
    }

    public Payment getLastUserPaymentFromDb(Customer customer) {
        Payment payment;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            payment = session.createQuery("select p from Payment p where p.customer = :customer order by p.paymentDate desc limit 1", Payment.class)
                    .setParameter("customer", customer)
                    .getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new NoSuchPaymentException("Payment not found");
        }
        return payment;
    }

    public void addNewFilmToStore(String title, String description, LocalDate releaseDate, Language language, Short length, Float price) {
        Film film = Film.builder()
                .title(title)
                .description(description)
                .releaseYear(releaseDate.getYear())
                .language(language)
                .rentalDuration((short) 1)
                .rentalRate((float) 1)
                .length(length)
                .replacementCost(price)
                .build();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            session.merge(film);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    public Customer getCustomerFromDb(String email) {
        Customer customer;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            customer = session.createQuery("select distinct c from Customer c where c.email like :email", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new NoSuchCustomerFound("Customer with e-mail " + email + " not found");
        }
        return customer;
    }

    public Film getFilmFromDb(String title) {
        Film film;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try (session) {
            film = session.createQuery("select f from Film f where f.title like :title", Film.class)
                    .setParameter("title", title)
                    .getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new NoSuchFilmFound("Film " + title + " not found");
        }
        return film;
    }

    private static Rental getRental(Customer customer, Staff staff, Inventory inventory) {
        return Rental.builder()
                .rentalDate(LocalDateTime.now())
                .inventory(inventory)
                .customer(customer)
                .staff(staff)
                .build();
    }

    private static Payment getPayment(Customer customer, Staff staff, Float amount, Rental rental, LocalDateTime date) {
        return Payment.builder()
                .customer(customer)
                .staff(staff)
                .rental(rental)
                .paymentDate(date)
                .amount(amount)
                .build();
    }
}
