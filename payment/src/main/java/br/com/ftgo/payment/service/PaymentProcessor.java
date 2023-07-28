package br.com.ftgo.payment.service;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.gateway.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PaymentProcessor {
    @Autowired
    private static CreditCardProcessor creditCardProcessor;

    @Autowired
    private static BankSlipProcessor bankSlipProcessor;

    public abstract Invoice processPayment(Order order, PaymentGateway gateway);

    public static PaymentProcessor create(String paymentType) {
        switch (paymentType) {
            case "credit_card":
                return creditCardProcessor;
            case "bank_slip":
                return bankSlipProcessor;
            default:
                throw new RuntimeException("invalid payment method");
        }
    }
}
