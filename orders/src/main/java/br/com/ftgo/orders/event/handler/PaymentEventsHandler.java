package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.dto.Invoice;
import br.com.ftgo.orders.entity.Order;
import br.com.ftgo.orders.entity.OrderStatus;
import br.com.ftgo.orders.event.ContextHandler;
import br.com.ftgo.orders.repository.OrdersRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PaymentEventsHandler {
    private OrdersRepository repository;

    private ContextHandler contextHandler;

    public PaymentEventsHandler(OrdersRepository repository, ContextHandler contextHandler) {
        this.repository = repository;
        this.contextHandler = contextHandler;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.invoice.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "invoice.*"

    ))
    public void handleInvoiceEvents(Invoice invoice, Message message) {
        contextHandler.withMessageContext(message, span ->
                repository.findById(invoice.orderId())
                        .switchIfEmpty(Mono.error(new AmqpRejectAndDontRequeueException("order not found")))
                        .map(order -> {
                            order.setStatus(OrderStatus.valueOf(invoice.status().toUpperCase()));
                            order.setInvoiceUrl(invoice.paymentUrl());
                            return order;
                        })
                        .flatMap(repository::save)
                        .doOnError(exception -> span.recordException(exception))
                        .block()
        );
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.payment.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.*"
    ))
    public void handlePaymentEvents(Order order, Message message) {
        contextHandler.withMessageContext(message, span ->
                repository.findById(order.getId())
                        .switchIfEmpty(Mono.error(new AmqpRejectAndDontRequeueException("order not found")))
                        .map(entity -> {
                            entity.setStatus(order.getStatus());
                            return entity;
                        })
                        .flatMap(repository::save)
                        .doOnError(exception -> span.recordException(exception))
                        .block()
        );
    }
}
