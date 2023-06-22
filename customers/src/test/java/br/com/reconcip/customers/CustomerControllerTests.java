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
}
