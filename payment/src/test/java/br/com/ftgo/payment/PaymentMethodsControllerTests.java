package br.com.ftgo.payment;

import br.com.ftgo.payment.controller.PaymentMethodsController;
import br.com.ftgo.payment.entity.Customer;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.CustomersRepository;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class PaymentMethodsControllerTests {
    private MockMvc client;

    @Autowired
    private PaymentMethodsRepository repository;

    @Autowired
    private CustomersRepository customersRepository;

    private PaymentGateway gateway;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        customersRepository.deleteAll();

        gateway = Mockito.spy(PaymentGateway.class);
        client = MockMvcBuilders.standaloneSetup( new PaymentMethodsController(gateway,repository)).build();
    }

    @Test
    void listEmpty() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setGatewayId("someuuid");

        customersRepository.save(customer);

        client.perform(
                MockMvcRequestBuilders.get("/payment-methods?customer=" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void listNotEmpty() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setGatewayId("someuuid");

        customersRepository.save(customer);

        PaymentMethod method = new PaymentMethod();

        method.setGatewayId("someuuid");
        method.setDisplayNumber("somemaskednumber");
        method.setPaymentType("credit_card");
        method.setCustomer(customer);

        repository.save(method);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setGatewayId("someotheruuid");

        customersRepository.save(customer2);

        PaymentMethod method2 = new PaymentMethod();

        method2.setGatewayId("someotheruuid");
        method2.setDisplayNumber("someothermaskednumber");
        method2.setPaymentType("credit_card");
        method2.setCustomer(customer2);

        repository.save(method2);

        client.perform(
                MockMvcRequestBuilders.get("/payment-methods?customer=" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$[0]['gatewayId']").value("someuuid"),
                        MockMvcResultMatchers.jsonPath("$[0]['displayNumber']").value("somemaskednumber"),
                        MockMvcResultMatchers.jsonPath("$[0]['paymentType']").value("credit_card"),
                        MockMvcResultMatchers.jsonPath("$[1]").doesNotExist()
                );
    }

    @Test
    void getInvalid() throws Exception {
        client.perform(
                MockMvcRequestBuilders.get("/payment-methods/invalidid")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getNonExistent() throws Exception {
        client.perform(
                        MockMvcRequestBuilders.get("/payment-methods/42069")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void get() throws Exception {
        PaymentMethod method = new PaymentMethod();

        method.setGatewayId("someuuid");
        method.setDisplayNumber("somemaskednumber");
        method.setPaymentType("credit_card");

        repository.save(method);

        client.perform(
                        MockMvcRequestBuilders.get("/payment-methods/" + method.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("id").value(method.getId()),
                        MockMvcResultMatchers.jsonPath("gatewayId").value("someuuid"),
                        MockMvcResultMatchers.jsonPath("displayNumber").value("somemaskednumber"),
                        MockMvcResultMatchers.jsonPath("paymentType").value("credit_card"),
                        MockMvcResultMatchers.jsonPath("description").isEmpty()
                );
    }

    @Test
    void deleteInvalid() throws Exception {
        client.perform(
                MockMvcRequestBuilders.delete("/payment-methods/aoeustnh")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteNotFound() throws Exception {
        client.perform(
                        MockMvcRequestBuilders.delete("/payment-methods/1523")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteExisting() throws Exception {
        PaymentMethod method = new PaymentMethod();
        method.setGatewayId("someuuid");
        method.setPaymentType("credit_card");
        method.setDisplayNumber("somemaskednumber");

        repository.save(method);

        client.perform(
                        MockMvcRequestBuilders.delete("/payment-methods/" + method.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(gateway, Mockito.times(1)).deletePaymentMethod(Mockito.any());
    }

    @Test
    void changeDefaultMethod() throws Exception {
        PaymentMethod method = new PaymentMethod();
        method.setDefault(true);
        method.setGatewayId("someuuid");
        method.setPaymentType("credit_card");
        method.setDisplayNumber("somemaskednumber");

        repository.save(method);

        PaymentMethod other = new PaymentMethod();
        other.setDefault(false);
        other.setGatewayId("someotheruuid");
        other.setPaymentType("credit_card");
        other.setDisplayNumber("someothermaskednumber");

        repository.save(other);

        client.perform(MockMvcRequestBuilders.put("/payment-methods/" + other.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertEquals(other.getId(), repository.findByIsDefault(true).get().getId());
        Assertions.assertEquals(method.getId(), repository.findByIsDefault(false).get().getId());
    }
}
