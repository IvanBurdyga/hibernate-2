package repository;

import entity.Address;
import entity.Customer;
import entity.Store;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class RepositoryTest extends TestContainer {

    private final static JdbcDatabaseContainer<?> CONTAINER;
    private final static String MYSQL_IMAGE_NAME = "mysql:8.0";
    public final static SessionFactory sessionFactory;
    private Session session;

    static {
        CONTAINER = new MySQLContainer<>(MYSQL_IMAGE_NAME);
        CONTAINER.start();

        Properties properties = new Properties();
        properties.put(Environment.JAKARTA_JDBC_URL, CONTAINER.getJdbcUrl());
        properties.put(Environment.JAKARTA_JDBC_USER, CONTAINER.getUsername());
        properties.put(Environment.JAKARTA_JDBC_PASSWORD, CONTAINER.getPassword());

        sessionFactory = new Configuration()
                .setProperties(properties)
                .buildSessionFactory();
    }

    @BeforeEach
    void setupThis(){
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    @AfterEach
    void tearThis(){
        session.getTransaction().commit();
    }

    @AfterAll
    static void tear(){
        sessionFactory.close();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Alex, Musk, ex@m.com"
    })
    void addNewCustomer(String firstName, String lastName, String email) {
        //given
        Repository repo = new Repository();
        //when
        Customer customer = repo.addNewCustomer(firstName,lastName,email,new Store(),new Address());
        //then
        Customer customer1 = session.createQuery("select c from Customer c where c.firstName = :firstName and c.lastName = :lastName", Customer.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
        Assertions.assertEquals(customer, customer1);
    }

    @Test
    void returnFilmToStore() {
    }

    @Test
    void rentalInventory() {
    }

    @Test
    void addNewFilmToStore() {
    }
}