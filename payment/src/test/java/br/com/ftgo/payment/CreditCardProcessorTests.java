package br.com.ftgo.payment;

import br.com.ftgo.payment.dto.Address;
import br.com.ftgo.payment.dto.CardInformation;
import br.com.ftgo.payment.dto.Customer;
import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.exception.PaymentMethodNotFoundException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.CustomersRepository;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import br.com.ftgo.payment.service.CreditCardProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreditCardProcessorTests {
    private CreditCardProcessor processor;

    private PaymentGateway gateway;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PaymentMethodsRepository methodsRepository;

    @BeforeEach
    void setup() {
        customersRepository.deleteAll();
        gateway = Mockito.mock(PaymentGateway.class);
        processor = new CreditCardProcessor(customersRepository, methodsRepository);
    }

    @Test
    void processBankSlip() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("bank_slip", "", customer, cardInfo, null);

        Assertions.assertThrows(RuntimeException.class, () -> processor.processPayment(order, gateway));
    }

    @Test
    void processCustomerNotFound() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "", customer, cardInfo, null);

        Mockito.when(gateway.createCustomer(Mockito.any())).thenReturn(new br.com.ftgo.payment.entity.Customer());
        Mockito.when(gateway.createPaymentMethod(Mockito.any(), Mockito.any())).thenReturn(new PaymentMethod());

        processor.processPayment(order, gateway);
        Mockito.verify(gateway, Mockito.times(1)).createCustomer(Mockito.any());
    }

    @Test
    void processCustomerFound() {
        Customer customer = new Customer(2L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "", customer, cardInfo, null);

        br.com.ftgo.payment.entity.Customer c = new br.com.ftgo.payment.entity.Customer();
        c.setId(2L);
        c.setGatewayId("something");
        customersRepository.save(c);

        Mockito.when(gateway.createPaymentMethod(Mockito.any(), Mockito.any())).thenReturn(new PaymentMethod());

        processor.processPayment(order, gateway);
        Mockito.verify(gateway, Mockito.times(0)).createCustomer(Mockito.any());
    }

    @Test
    void processWithPaymentMethodIdNotFound() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "someid", customer, cardInfo, null);

        Mockito.when(gateway.createCustomer(Mockito.any())).thenReturn(new br.com.ftgo.payment.entity.Customer());

        Assertions.assertThrows(PaymentMethodNotFoundException.class, () -> processor.processPayment(order, gateway));
    }

    @Test
    void processWithPaymentMethodIdFound() {
        PaymentMethod method = new PaymentMethod();

        method.setId(1L);
        method.setDescription("test card");
        method.setPaymentType("credit_card");
        method.setDisplayNumber("XXXX-XXXX-XXXX-0025");
        method.setGatewayId("3ad199bd-b1e5-437c-8414-e7a7b216e03d");

        methodsRepository.save(method);

        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "3ad199bd-b1e5-437c-8414-e7a7b216e03d", customer, cardInfo, null);

        Mockito.when(gateway.createCustomer(Mockito.any())).thenReturn(new br.com.ftgo.payment.entity.Customer());

        Assertions.assertDoesNotThrow(() -> processor.processPayment(order, gateway));
        Mockito.verify(gateway, Mockito.times(0)).createPaymentMethod(Mockito.any(), Mockito.any());
    }

    @Test
    void processWithoutPaymentMethodId() {
        Customer customer = new Customer(1L, "John Doe", "111.502.443-22", new Address("rua", "numero", "bairro", "cidade", "uf", "cep"));
        CardInformation cardInfo = new CardInformation("holder", "number", "expdate", "cvv");
        Order order = new Order("credit_card", "", customer, cardInfo, null);

        Mockito.when(gateway.createCustomer(Mockito.any())).thenReturn(new br.com.ftgo.payment.entity.Customer());

        PaymentMethod method = new PaymentMethod();

        method.setId(2L);
        method.setDescription("test card");
        method.setPaymentType("debit_card");
        method.setDisplayNumber("XXXX-XXXX-XXXX-0030");
        method.setGatewayId("55ddf575-5988-4472-9d78-5080a3b536b0");

        Mockito.when(gateway.createPaymentMethod(Mockito.any(), Mockito.any())).thenReturn(method);

        processor.processPayment(order, gateway);
        Mockito.verify(gateway, Mockito.times(1)).createPaymentMethod(Mockito.any(), Mockito.any());

        Assertions.assertTrue(methodsRepository.existsByGatewayId("55ddf575-5988-4472-9d78-5080a3b536b0"));
    }
}
