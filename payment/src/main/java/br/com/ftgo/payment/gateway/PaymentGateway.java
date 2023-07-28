package br.com.ftgo.payment.gateway;

import br.com.ftgo.payment.dto.CardInformation;
import br.com.ftgo.payment.dto.Customer;
import br.com.ftgo.payment.dto.Item;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.PaymentMethod;

import java.util.List;

public interface PaymentGateway {
    br.com.ftgo.payment.entity.Customer createCustomer(Customer customer);

    Invoice createInvoice(String customerId, String paymentType, String paymentMethodId, List<Item> items);

    Invoice createInvoice(String customerId, String paymentType, List<Item> items);

    PaymentMethod createPaymentMethod(String customerId, CardInformation card);
}
