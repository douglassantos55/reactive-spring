package br.com.ftgo.payment.dto;

import java.util.List;

public record Order(String id, String paymentType, String paymentMethodId, Customer customer, CardInformation card, List<Item> items) {
    public boolean hasPaymentMethodId() {
        return paymentMethodId != null && !paymentMethodId.isEmpty();
    }

    public boolean isCreditCard() {
        return paymentType.equals("credit_card");
    }
}
