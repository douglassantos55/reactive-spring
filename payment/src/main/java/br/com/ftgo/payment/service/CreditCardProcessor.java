package br.com.ftgo.payment.service;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Customer;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.exception.PaymentMethodNotFoundException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.CustomersRepository;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;

public class CreditCardProcessor extends PaymentProcessor {
    private CustomersRepository customersRepository;

    private PaymentMethodsRepository methodsRepository;

    public CreditCardProcessor(CustomersRepository customersRepository, PaymentMethodsRepository methodsRepository) {
        this.customersRepository = customersRepository;
        this.methodsRepository = methodsRepository;
    }

    public Invoice processPayment(Order order, PaymentGateway gateway) {
        Customer customer = customersRepository
                .findById(order.customer().id())
                .orElseGet(() -> gateway.createCustomer(order.customer()));

        String paymentMethodId;

        if (order.hasPaymentMethodId()) {
            if (!methodsRepository.existsByGatewayId(order.paymentMethodId())) {
                throw new PaymentMethodNotFoundException();
            }
            paymentMethodId = order.paymentMethodId();
        } else {
            PaymentMethod paymentMethod = gateway.createPaymentMethod(customer.getGatewayId(), order.info());
            paymentMethod = methodsRepository.save(paymentMethod);

//            saveNotification("payment_method.created", paymentMethod);
            paymentMethodId = paymentMethod.getGatewayId();
        }

        return gateway.createInvoice(customer.getGatewayId(), order.paymentType(), paymentMethodId, order.items());
    }
}
