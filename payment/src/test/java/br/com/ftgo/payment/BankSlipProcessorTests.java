package br.com.ftgo.payment;

import br.com.ftgo.payment.dto.*;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.CustomersRepository;
import br.com.ftgo.payment.service.BankSlipProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class BankSlipProcessorTests {
    private BankSlipProcessor processor;

    @Autowired
    private CustomersRepository customersRepository;

    private PaymentGateway gateway;

    @BeforeEach
    void setup() {
        gateway = Mockito.mock(PaymentGateway.class);
        processor = new BankSlipProcessor(customersRepository);
    }

    @Test
    void processCreditCard() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "", customer, cardInfo, null);

        Assertions.assertThrows(RuntimeException.class, () -> processor.processPayment(order, gateway));
    }

    @Test
    void processCustomerNotFound() {
        Customer customer = new Customer(2L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item("item", 1, 15.55));
        items.add(new Item("mouse", 1, 35.55));

        Order order = new Order("bank_slip", "", customer, cardInfo, items);

        br.com.ftgo.payment.entity.Customer c = new br.com.ftgo.payment.entity.Customer();
        c.setId(2L);
        c.setGatewayId("f8334ac8-0675-4606-811a-df279f617308");

        Mockito.when(gateway.createCustomer(Mockito.any())).thenReturn(c);
        Mockito.when(gateway.createInvoice("f8334ac8-0675-4606-811a-df279f617308", "bank_slip", items)).thenReturn(new Invoice());

        Invoice invoice = processor.processPayment(order, gateway);

        Assertions.assertNotNull(invoice);
        Mockito.verify(gateway, Mockito.times(1)).createCustomer(Mockito.any());
    }

    @Test
    void createCustomerFound() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item("item", 1, 15.55));
        items.add(new Item("mouse", 1, 35.55));

        Order order = new Order("bank_slip", "", customer, cardInfo, items);

        br.com.ftgo.payment.entity.Customer c = new br.com.ftgo.payment.entity.Customer();
        c.setId(1L);
        c.setGatewayId("f8334ac8-0675-4606-811a-df279f617308");
        customersRepository.save(c);

        Mockito.when(gateway.createInvoice("f8334ac8-0675-4606-811a-df279f617308", "bank_slip", items)).thenReturn(new Invoice());
        Invoice invoice = processor.processPayment(order, gateway);

        Assertions.assertNotNull(invoice);
        Mockito.verify(gateway, Mockito.times(0)).createCustomer(Mockito.any());
    }
}
