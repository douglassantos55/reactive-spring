package br.com.ftgo.payment;

import br.com.ftgo.payment.dto.*;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.event.ProcessPaymentHandler;
import br.com.ftgo.payment.exception.PaymentMethodNotFoundException;
import br.com.ftgo.payment.exception.UnexpectedPaymentTypeException;
import br.com.ftgo.payment.repository.InvoicesRepository;
import br.com.ftgo.payment.repository.MessagesRepository;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class ProcessPaymentHandlerTests {
    @Autowired
    private ProcessPaymentHandler handler;

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private PaymentMethodsRepository methodsRepository;

    @Autowired
    private InvoicesRepository invoicesRepository;

    @BeforeEach
    void setup() {
        methodsRepository.deleteAll();
        messagesRepository.deleteAll();
        invoicesRepository.deleteAll();
    }

    @Test
    void processInvalidPaymentType() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order1", "paypal", "", customer, cardInfo, items);

        Assertions.assertThrows(UnexpectedPaymentTypeException.class, () -> handler.processPayment(order, null));
        Assertions.assertTrue(messagesRepository.findByRoutingKey("payment.failed").isEmpty());
    }

    @Test
    void processBankSlip() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order2", "bank_slip", "", customer, cardInfo, items);

        Assertions.assertDoesNotThrow(() -> handler.processPayment(order, null));
        Assertions.assertFalse(messagesRepository.findByRoutingKey("invoice.created").isEmpty());

        // Make sure no payment method is created for bank_slip
        Assertions.assertEquals(0, methodsRepository.count());
    }

    @Test
    void processCreditCard() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order3", "credit_card", "", customer, cardInfo, items);

        Assertions.assertDoesNotThrow(() -> handler.processPayment(order, null));
        Assertions.assertFalse(messagesRepository.findByRoutingKey("invoice.created").isEmpty());

        // Make sure payment method is created for credit_card
        Assertions.assertEquals(1, methodsRepository.count());
    }

    @Test
    void nonExistingPaymentMethod() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order4", "credit_card", "t452U2", customer, cardInfo, items);

        Assertions.assertThrows(PaymentMethodNotFoundException.class, () -> handler.processPayment(order, null));
        Assertions.assertTrue(messagesRepository.findByRoutingKey("invoice.created").isEmpty());

        // Make sure payment method is not recreated
        Assertions.assertEquals(0, methodsRepository.count());
    }

    @Test
    void existingPaymentMethod() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        PaymentMethod method = new PaymentMethod();
        method.setPaymentType("credit_card");
        method.setGatewayId("82c7cae6-9f20-40ad-8bd7-18b292a00bb8");
        method.setDisplayNumber("XXXX XXXX XXXX 0023");
        method.setDescription("nubank");

        methodsRepository.save(method);

        Order order = new Order("order4", "credit_card", method.getGatewayId(), customer, cardInfo, items);

        Assertions.assertDoesNotThrow(() -> handler.processPayment(order, null));
        Assertions.assertFalse(messagesRepository.findByRoutingKey("invoice.created").isEmpty());

        // Make sure payment method is not recreated
        Assertions.assertEquals(1, methodsRepository.count());

        // Make sure it is assigned to invoice
        Invoice invoice = invoicesRepository.findByOrderId("order4").get();
        Assertions.assertEquals(method.getId(), invoice.getPaymentMethod().getId());
    }
}
