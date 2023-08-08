package br.com.ftgo.payment;

import br.com.ftgo.payment.controller.PaymentMethodsController;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.gateway.PaymentGateway;
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

    private PaymentGateway gateway;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        gateway = Mockito.spy(PaymentGateway.class);
        client = MockMvcBuilders.standaloneSetup( new PaymentMethodsController(gateway,repository)).build();
    }

    @Test
    void listEmpty() throws Exception {
        client.perform(
                MockMvcRequestBuilders.get("/payment-methods")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void listNotEmpty() throws Exception {
        PaymentMethod method = new PaymentMethod();

        method.setGatewayId("someuuid");
        method.setDisplayNumber("somemaskednumber");
        method.setPaymentType("credit_card");

        repository.save(method);

        client.perform(
                MockMvcRequestBuilders.get("/payment-methods")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$[0]['gatewayId']").value("someuuid"),
                        MockMvcResultMatchers.jsonPath("$[0]['displayNumber']").value("somemaskednumber"),
                        MockMvcResultMatchers.jsonPath("$[0]['paymentType']").value("credit_card")
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
