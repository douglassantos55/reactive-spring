package br.com.ftgo.payment;

import br.com.ftgo.payment.dto.*;
import br.com.ftgo.payment.event.ProcessPaymentHandler;
import br.com.ftgo.payment.exception.UnexpectedPaymentTypeException;
import br.com.ftgo.payment.repository.MessagesRepository;
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

    @BeforeEach
    void setup() {
        messagesRepository.deleteAll();
    }

    @Test
    void processInvalidPaymentType() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order1", "paypal", "", customer, cardInfo, items);

        Assertions.assertThrows(UnexpectedPaymentTypeException.class, () -> handler.processPayment(order));
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

        Assertions.assertDoesNotThrow(() -> handler.processPayment(order));
        Assertions.assertFalse(messagesRepository.findByRoutingKey("invoice.created").isEmpty());
    }

    @Test
    void processCreditCard() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("item 1", 2, 32.22));
        items.add(new Item("item 2", 5, 12.35));

        Order order = new Order("order3", "credit_card", "", customer, cardInfo, items);

        Assertions.assertDoesNotThrow(() -> handler.processPayment(order));
        Assertions.assertFalse(messagesRepository.findByRoutingKey("invoice.created").isEmpty());
    }
}
