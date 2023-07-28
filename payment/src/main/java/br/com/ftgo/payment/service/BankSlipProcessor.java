package br.com.ftgo.payment.service;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Customer;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.CustomersRepository;
import org.springframework.stereotype.Service;

@Service
public class BankSlipProcessor extends PaymentProcessor {
    private static String NAME = "bank_slip";

    private CustomersRepository customersRepository;

    public BankSlipProcessor(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public Invoice processPayment(Order order, PaymentGateway gateway) {
        if (!order.paymentType().equals(NAME)) {
            throw new RuntimeException("cannot process order");
        }

        Customer customer = customersRepository
                .findById(order.customer().id())
                .orElseGet(() -> gateway.createCustomer(order.customer()));

        return gateway.createInvoice(customer.getGatewayId(), order.paymentType(), order.items());
    }
}
