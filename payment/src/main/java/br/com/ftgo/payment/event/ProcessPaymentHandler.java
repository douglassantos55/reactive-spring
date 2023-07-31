package br.com.ftgo.payment.event;

import br.com.ftgo.payment.dto.Order;
import br.com.ftgo.payment.entity.Invoice;
import br.com.ftgo.payment.entity.Message;
import br.com.ftgo.payment.exception.GatewayException;
import br.com.ftgo.payment.exception.PaymentMethodNotFoundException;
import br.com.ftgo.payment.exception.UnexpectedPaymentTypeException;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.InvoicesRepository;
import br.com.ftgo.payment.repository.MessagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentHandler {
    private PaymentGateway gateway;

    private MessagesRepository messagesRepository;

    private ObjectMapper mapper;

    public ProcessPaymentHandler(
            PaymentGateway gateway,
            MessagesRepository messagesRepository,
            ObjectMapper mapper
    ) {
        this.gateway = gateway;
        this.mapper = mapper;
        this.messagesRepository = messagesRepository;
    }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "payment.process.queue", durable = "true"),
            exchange = @Exchange(name = "payment.exchange"),
            key = "payment.process"
    ))
    public void processPayment(Order order) throws GatewayException {
        try {
            if (!gateway.supports(order.paymentType())) {
                throw new UnexpectedPaymentTypeException();
            }

            Invoice invoice = gateway.processPayment(order);
            saveNotification("invoice.created", invoice);
        } catch (GatewayException exception) {
            saveNotification("payment.failed", order);
            throw exception;
        }
    }

    private void saveNotification(String routingKey, Object body) {
        try {
            Message message = new Message();

            message.setRoutingKey(routingKey);
            message.setExchange("notifications.exchange");
            message.setBody(mapper.writeValueAsBytes(body));

            messagesRepository.save(message);
        } catch (JsonProcessingException exception) {
            // could not do stuff...
        }
    }
}
