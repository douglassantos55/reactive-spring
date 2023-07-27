package br.com.fgto.customers;

import br.com.fgto.customers.entity.Address;
import br.com.fgto.customers.repository.CustomerRepository;
import br.com.fgto.customers.entity.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

@SpringBootTest(classes = AmqpTestConfiguration.class)
public class CustomerControllerTests {
    private MockMvc client;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        repository.deleteAll();
        client = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createValidationError() throws Exception {
        Customer customer = new Customer();
        customer.setName("");
        customer.setDocument("");
        customer.setBillingAddress(new Address());

        client.perform(
                        MockMvcRequestBuilders
                                .post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(customer))
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("errors.document").value("must not be empty"),
                        MockMvcResultMatchers.jsonPath("errors.name").value("must not be empty"),
                        MockMvcResultMatchers.jsonPath("$['errors']['billingAddress.street']").value("must not be empty")
                );
    }

    @Test
    void createNoDeliveryAddress() throws Exception {
        Customer customer = new Customer();
        customer.setName("john doe");
        customer.setDocument("830.139.280-00");
        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        client.perform(
                        MockMvcRequestBuilders
                                .post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(customer))
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("name").value("john doe"),
                        MockMvcResultMatchers.jsonPath("document").value("830.139.280-00"),

                        MockMvcResultMatchers.jsonPath("billingAddress.street").value("rua"),
                        MockMvcResultMatchers.jsonPath("billingAddress.number").value("530"),
                        MockMvcResultMatchers.jsonPath("billingAddress.neighborhood").value("centro"),
                        MockMvcResultMatchers.jsonPath("billingAddress.city").value("Sao Paulo"),
                        MockMvcResultMatchers.jsonPath("billingAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("billingAddress.zipcode").value("01010-000"),

                        MockMvcResultMatchers.jsonPath("deliveryAddress.street").value("rua"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.number").value("530"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.neighborhood").value("centro"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.city").value("Sao Paulo"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.zipcode").value("01010-000")
                );
    }

    @Test
    void createWithDeliveryAddress() throws Exception {
        Customer customer = new Customer();
        customer.setName("john doe");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        Address delivery = new Address("delivery", "33", "suburbs", "Campinas", "SP", "13052-522");
        customer.setDeliveryAddress(delivery);

        client.perform(
                        MockMvcRequestBuilders
                                .post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(customer))
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("name").value("john doe"),
                        MockMvcResultMatchers.jsonPath("document").value("830.139.280-00"),

                        MockMvcResultMatchers.jsonPath("billingAddress.street").value("rua"),
                        MockMvcResultMatchers.jsonPath("billingAddress.number").value("530"),
                        MockMvcResultMatchers.jsonPath("billingAddress.neighborhood").value("centro"),
                        MockMvcResultMatchers.jsonPath("billingAddress.city").value("Sao Paulo"),
                        MockMvcResultMatchers.jsonPath("billingAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("billingAddress.zipcode").value("01010-000"),

                        MockMvcResultMatchers.jsonPath("deliveryAddress.street").value("delivery"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.number").value("33"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.neighborhood").value("suburbs"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.city").value("Campinas"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.zipcode").value("13052-522")
                );
    }

    @Test
    void createInvalidDocument() throws Exception {
        Customer customer = new Customer();
        customer.setName("john doe");
        customer.setDocument("830.139.280-10");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        client.perform(
                        MockMvcRequestBuilders
                                .post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(customer))
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("errors.document").value("invalid document")
                );
    }

    @Test
    void list() throws Exception {
        Customer c1 = new Customer();
        c1.setName("john 1");
        c1.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        c1.setBillingAddress(billing);

        repository.save(c1);

        Customer c2 = new Customer();
        c2.setName("john 2");
        c2.setDocument("614.041.370-25");

        Address billing2 = new Address("john 2", "1530", "centro", "Sao Paulo", "SP", "01010-000");
        c2.setBillingAddress(billing2);

        Address delivery = new Address("delivery", "111", "centro", "Mogi Mirim", "SP", "13000-000");
        c2.setDeliveryAddress(delivery);

        repository.save(c2);

        Customer c3 = new Customer();
        c3.setName("john 3");
        c3.setDocument("670.788.270-82");

        Address billing3 = new Address("billing deleted", "111", "centro", "Mogi Mirim", "SP", "13000-000");
        c3.setBillingAddress(billing3);

        Address delivery2 = new Address("delivery deleted", "111", "centro", "Mogi Mirim", "SP", "13000-000");
        c3.setDeliveryAddress(delivery2);

        c3.setDeletedAt(Instant.now());

        repository.save(c3);

        client.perform(
                        MockMvcRequestBuilders
                                .get("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$[0]['name']").value("john 1"),
                        MockMvcResultMatchers.jsonPath("$[0]['document']").value("830.139.280-00"),
                        MockMvcResultMatchers.jsonPath("$[0]['billingAddress']['street']").value("rua"),

                        MockMvcResultMatchers.jsonPath("$[1]['name']").value("john 2"),
                        MockMvcResultMatchers.jsonPath("$[1]['document']").value("614.041.370-25"),
                        MockMvcResultMatchers.jsonPath("$[1]['billingAddress']['street']").value("john 2"),

                        MockMvcResultMatchers.jsonPath("$[2]").doesNotExist()
                );
    }

    @Test
    void getNonExistent() throws Exception {
        client.perform(
                        MockMvcRequestBuilders
                                .get("/customers/1000")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getInvalid() throws Exception {
        client.perform(
                        MockMvcRequestBuilders
                                .get("/customers/something")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getExisting() throws Exception {
        Customer customer = new Customer();
        customer.setName("john 1");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        Customer created = repository.save(customer);

        client.perform(
                        MockMvcRequestBuilders
                                .get("/customers/" + created.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("name").value("john 1"),
                        MockMvcResultMatchers.jsonPath("document").value("830.139.280-00"),

                        MockMvcResultMatchers.jsonPath("billingAddress.street").value("rua"),
                        MockMvcResultMatchers.jsonPath("billingAddress.number").value("530"),
                        MockMvcResultMatchers.jsonPath("billingAddress.neighborhood").value("centro"),
                        MockMvcResultMatchers.jsonPath("billingAddress.city").value("Sao Paulo"),
                        MockMvcResultMatchers.jsonPath("billingAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("billingAddress.zipcode").value("01010-000"),

                        MockMvcResultMatchers.jsonPath("deliveryAddress.street").value("rua"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.number").value("530"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.neighborhood").value("centro"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.city").value("Sao Paulo"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.state").value("SP"),
                        MockMvcResultMatchers.jsonPath("deliveryAddress.zipcode").value("01010-000")
                );
    }

    @Test
    void deleteNonExisting() throws Exception {
        client.perform(MockMvcRequestBuilders.delete("/customers/1000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteInvalid() throws Exception {
        client.perform(MockMvcRequestBuilders.delete("/customers/something"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteExisting() throws Exception {
        Customer customer = new Customer();
        customer.setName("foobar");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        Customer created = repository.save(customer);

        client.perform(MockMvcRequestBuilders.delete("/customers/" + created.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        client.perform(
                MockMvcRequestBuilders
                        .get("/customers/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateNonExisting() throws Exception {
        Customer customer = new Customer();
        customer.setName("foobar");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        client.perform(
                MockMvcRequestBuilders
                        .put("/customers/157")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer))
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateInvalid() throws Exception {
        Customer customer = new Customer();
        customer.setName("foobar");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        client.perform(
                MockMvcRequestBuilders
                        .put("/customers/something")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateExisting() throws Exception {
        Customer customer = new Customer();
        customer.setName("foobar");
        customer.setDocument("830.139.280-00");

        Address billing = new Address("rua", "530", "centro", "Sao Paulo", "SP", "01010-000");
        customer.setBillingAddress(billing);

        Customer created = repository.save(customer);

        Customer data = new Customer();
        data.setName("should be updated");
        data.setDocument("614.041.370-25");
        data.setBillingAddress(new Address("billing", "000", "cambui", "campinas", "SP", "13800-000"));
        data.setDeliveryAddress(new Address("delivery", "300", "centro", "sao paulo", "sp", "13800-122"));

        client.perform(
                MockMvcRequestBuilders
                        .put("/customers/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(data))
        ).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.jsonPath("name").value("should be updated"),
                MockMvcResultMatchers.jsonPath("document").value("614.041.370-25"),

                MockMvcResultMatchers.jsonPath("billingAddress.street").value("billing"),
                MockMvcResultMatchers.jsonPath("billingAddress.number").value("000"),
                MockMvcResultMatchers.jsonPath("billingAddress.neighborhood").value("cambui"),
                MockMvcResultMatchers.jsonPath("billingAddress.city").value("campinas"),
                MockMvcResultMatchers.jsonPath("billingAddress.state").value("SP"),
                MockMvcResultMatchers.jsonPath("billingAddress.zipcode").value("13800-000"),

                MockMvcResultMatchers.jsonPath("deliveryAddress.street").value("delivery"),
                MockMvcResultMatchers.jsonPath("deliveryAddress.number").value("300"),
                MockMvcResultMatchers.jsonPath("deliveryAddress.neighborhood").value("centro"),
                MockMvcResultMatchers.jsonPath("deliveryAddress.city").value("sao paulo"),
            MockMvcResultMatchers.jsonPath("deliveryAddress.state").value("sp"),
                MockMvcResultMatchers.jsonPath("deliveryAddress.zipcode").value("13800-122")
        );
    }
}
