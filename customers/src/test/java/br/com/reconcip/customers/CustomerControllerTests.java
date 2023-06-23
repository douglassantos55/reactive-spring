package br.com.reconcip.customers;

import br.com.reconcip.customers.entity.Customer;
import br.com.reconcip.customers.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;

@SpringBootTest
public class CustomerControllerTests {
    private WebTestClient client;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void setUp(ApplicationContext context) {
        repository.deleteAll().block();
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void createValidationError() {
        Customer customer = new Customer();
        customer.setName(" ");

        client.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new Customer())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("{\"errors\":{\"name\":\"must not be empty\",\"billingAddress\":\"must not be empty\"}}");

    }

    @Test
    void createNoDeliveryAddress() {
        Customer customer = new Customer();
        customer.setName("john doe");
        customer.setBillingAddress("new york city");

        client.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("{\"name\":\"john doe\",\"billingAddress\":\"new york city\",\"deliveryAddress\":\"new york city\"}");
    }

    @Test
    void createWithDeliveryAddress() {
        Customer customer = new Customer();
        customer.setName("john doe");
        customer.setBillingAddress("new york");
        customer.setDeliveryAddress("brooklyn");

        client.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("{\"name\":\"john doe\",\"deliveryAddress\":\"brooklyn\",\"billingAddress\":\"new york\"}");
    }

    @Test
    void list() {
        Customer c1 = new Customer();
        c1.setName("john 1");
        c1.setBillingAddress("san francisco");
        repository.save(c1).block();

        Customer c2 = new Customer();
        c2.setName("john 2");
        c2.setBillingAddress("san francisco");
        c2.setDeliveryAddress("california");
        repository.save(c2).block();

        Customer c3 = new Customer();
        c3.setName("john 3");
        c3.setBillingAddress("san francisco");
        c3.setDeliveryAddress("new york");
        c3.setDeletedAt(Instant.now());
        repository.save(c3).block();

        client.get()
                .uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[" +
                        "{\"id\":2,\"name\":\"john 1\",\"billingAddress\":\"san francisco\",\"deliveryAddress\":\"san francisco\"}," +
                        "{\"id\":3,\"name\":\"john 2\",\"billingAddress\":\"san francisco\",\"deliveryAddress\":\"california\"}" +
                "]");
    }

    @Test
    void getNonExistent() {
        client.get()
                .uri("/customers/10000")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getInvalid() {
        client.get()
                .uri("/customers/something")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void getExisting() {
        Customer customer = new Customer();
        customer.setName("john 1");
        customer.setBillingAddress("san francisco");
        repository.save(customer).block();

        client.get()
                .uri("/customers/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{}");
    }

    @Test
    void deleteNonExisting() {
        client.delete()
                .uri("/customers/1000")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void deleteInvalid() {
        client.delete()
                .uri("/customers/something")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void deleteExisting() {
        Customer customer = new Customer();
        customer.setName("foobar");
        customer.setBillingAddress("new york");

        Customer created = repository.save(customer).block();

        client.delete()
                .uri("/customers/"+created.getId())
                .exchange()
                .expectStatus()
                .isNoContent();

        client.get()
                .uri("/customers/" + created.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }
}
