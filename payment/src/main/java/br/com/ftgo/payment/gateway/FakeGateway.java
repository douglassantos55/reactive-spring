package br.com.ftgo.payment.gateway;

import br.com.ftgo.payment.dto.CardInformation;
import br.com.ftgo.payment.dto.Item;
import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Customer;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.exception.GatewayException;
import br.com.ftgo.payment.exception.PaymentMethodNotFoundException;
import br.com.ftgo.payment.repository.CustomersRepository;
import br.com.ftgo.payment.repository.InvoicesRepository;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class FakeGateway implements PaymentGateway {
    private CustomersRepository customersRepository;

    private PaymentMethodsRepository methodsRepository;

    private InvoicesRepository invoicesRepository;

    public FakeGateway(CustomersRepository customers, PaymentMethodsRepository methods, InvoicesRepository invoices) {
        methodsRepository = methods;
        invoicesRepository = invoices;
        customersRepository = customers;
    }

    public boolean supports(String paymentType) {
        return paymentType.equals("bank_slip") || paymentType.equals("credit_card");
    }

    public Invoice refund(Invoice invoice, double amount) {
        invoice.setStatus("refunded");
        return invoicesRepository.save(invoice);
    }

    public void deletePaymentMethod(PaymentMethod method) {
        // do stuff and all
    }

    public Invoice processPayment(Order order) throws GatewayException {
        Customer customer = customersRepository
                .findById(order.customer().id())
                .orElseGet(() -> createCustomer(order.customer()));

        PaymentMethod paymentMethod = null;

        if (order.hasPaymentMethodId()) {
            paymentMethod = methodsRepository.findByGatewayId(order.paymentMethodId())
                    .orElseThrow(() -> new PaymentMethodNotFoundException());
        } else if (order.isCreditCard()) {
            paymentMethod = createPaymentMethod(customer, order.card());
        }

        Invoice invoice = createInvoice(order);
        invoice.setPaymentMethod(paymentMethod);

        return invoice;
    }

    private Customer createCustomer(br.com.ftgo.payment.dto.Customer customerDto) {
        Customer customer = new Customer();

        customer.setId(customerDto.id());
        customer.setGatewayId(UUID.randomUUID().toString());

        return customersRepository.save(customer);
    }

    private PaymentMethod createPaymentMethod(Customer customer, CardInformation cardInformation) {
        PaymentMethod method = new PaymentMethod();

        method.setCustomer(customer);
        method.setPaymentType("credit_card");
        method.setGatewayId(UUID.randomUUID().toString());

        String cardNumber = cardInformation.number();
        method.setDisplayNumber("XXXX XXXX XXXX " + cardNumber.substring(cardNumber.length() - 4));

        return methodsRepository.save(method);
    }

    private Invoice createInvoice(Order order) {
        Invoice invoice = new Invoice();

        for (Item item : order.items()) {
            invoice.setTotal(invoice.getTotal() + item.price() * item.qty());
        }

        if (order.paymentType().equals("credit_card")) {
            invoice.setStatus("paid");
        } else {
            invoice.setStatus("pending");
        }

        invoice.setOrderId(order.id());
        invoice.setCustomerId(order.customer().id());
        invoice.setGatewayId(UUID.randomUUID().toString());
        invoice.setDueDate(Instant.now().plus(3, ChronoUnit.DAYS));
        invoice.setPaymentUrl("https://fake-gateway.com/invoice/" + invoice.getGatewayId());

        return invoicesRepository.save(invoice);
    }
}
