package br.com.ftgo.payment;

import br.com.ftgo.payment.controller.PaymentMethodsController;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        client = MockMvcBuilders.standaloneSetup(new PaymentMethodsController(repository)).build();
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
}
