package br.com.ftgo.payment.event;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.exception.UnexpectedPaymentTypeException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentHandler {
    private PaymentGateway gateway;

    private Messenger messenger;

    private ContextHandler contextHandler;

    public ProcessPaymentHandler(PaymentGateway gateway, Messenger messenger, ContextHandler contextHandler) {
        this.gateway = gateway;
        this.messenger = messenger;
        this.contextHandler = contextHandler;
    }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "payment.process.queue", durable = "true"),
            exchange = @Exchange(name = "payment.exchange"),
            key = "payment.process"
    ))
    public void processPayment(Order order, Message message) {
        contextHandler.withMessageContext(message, span -> {
            try {
                if (!gateway.supports(order.paymentType())) {
                    throw new UnexpectedPaymentTypeException();
                }

                Invoice invoice = gateway.processPayment(order);
                messenger.saveMessage("invoice.created", "notifications.exchange", invoice);
            } catch (Exception exception) {
                try {
                    span.recordException(exception);
                    messenger.saveMessage("payment.failed", "notifications.exchange", order);
                } catch (JsonProcessingException e) {
                    span.recordException(e);
                }
            } finally {
                return null;
            }
        });
    }
}
