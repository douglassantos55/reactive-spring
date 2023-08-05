package br.com.ftgo.payment.gateway;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.exception.GatewayException;

public interface PaymentGateway {
    Invoice processPayment(Order order) throws GatewayException;

    Invoice refund(Invoice invoice, double amount) throws GatewayException;

    boolean supports(String paymentType);
}
