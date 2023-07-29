package br.com.ftgo.payment.gateway;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;

public interface PaymentGateway {
    Invoice processPayment(Order order);

    boolean supports(String paymentType);
}
