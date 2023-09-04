package br.com.ftgo.payment.event;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.exception.GatewayException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.InvoicesRepository;
import br.com.ftgo.payment.repository.MessagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class RefundPaymentHandler {
    private InvoicesRepository invoicesRepository;

    private PaymentGateway gateway;

    private Messenger messenger;

    private ContextHandler contextHandler;

    public RefundPaymentHandler(PaymentGateway gateway, InvoicesRepository invoicesRepository, Messenger messenger, ContextHandler contextHandler) {
        this.gateway = gateway;
        this.messenger = messenger;
        this.contextHandler = contextHandler;
        this.invoicesRepository = invoicesRepository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "payment.refund.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "order.cancelled"
    ))
    public void refundPayment(Order order, Message message) throws GatewayException {
        contextHandler.withMessageContext(message, span -> {
            try {
                Invoice invoice = invoicesRepository.findByOrderId(order.id()).orElseThrow();
                invoice = gateway.refund(invoice, invoice.getTotal());

                messenger.saveMessage("invoice.refunded", "notifications.exchange", invoice);
            } catch (JsonProcessingException | NoSuchElementException exception) {
                // could not do stuff
                span.recordException(exception);
            } finally {
                return null;
            }
        });
    }
}
