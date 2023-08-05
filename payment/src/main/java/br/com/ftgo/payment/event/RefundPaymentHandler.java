package br.com.ftgo.payment.event;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.Message;
import br.com.ftgo.payment.exception.GatewayException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.InvoicesRepository;
import br.com.ftgo.payment.repository.MessagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class RefundPaymentHandler {
    private InvoicesRepository invoicesRepository;

    private MessagesRepository messagesRepository;

    private PaymentGateway gateway;

    private ObjectMapper mapper;

    public RefundPaymentHandler(PaymentGateway gateway, ObjectMapper mapper, InvoicesRepository invoicesRepository, MessagesRepository messagesRepository) {
        this.gateway = gateway;
        this.mapper = mapper;
        this.invoicesRepository = invoicesRepository;
        this.messagesRepository = messagesRepository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "payment.refund.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "order.cancelled"
    ))
    public void refundPayment(Order order) throws GatewayException {
        try {
            Invoice invoice = invoicesRepository.findByOrderId(order.id()).orElseThrow();
            invoice = gateway.refund(invoice, invoice.getTotal());

            Message message = new Message();
            message.setRoutingKey("invoice.refunded");
            message.setExchange("notifications.exchange");
            message.setBody(mapper.writeValueAsBytes(invoice));

            messagesRepository.save(message);
        } catch (JsonProcessingException | NoSuchElementException exception) {
            // could not do stuff
        }
    }
}
