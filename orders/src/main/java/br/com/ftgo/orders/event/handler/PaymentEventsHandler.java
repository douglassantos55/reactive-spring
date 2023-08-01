package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.dto.Invoice;
import br.com.ftgo.orders.entity.Order;
import br.com.ftgo.orders.entity.OrderStatus;
import br.com.ftgo.orders.repository.OrdersRepository;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsHandler {
    private OrdersRepository repository;

    public PaymentEventsHandler(OrdersRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.invoice.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "invoice.*"

    ))
    public void handleInvoiceEvents(Invoice invoice) {
        repository.findById(invoice.orderId())
                .map(order -> {
                    order.setStatus(OrderStatus.valueOf(invoice.status().toUpperCase()));
                    return order;
                })
                .flatMap(repository::save)
                .subscribe();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.payment.queue", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.*"
    ))
    public void handlePaymentEvents(Order order) {
        repository.findById(order.getId())
                .map(entity -> {
                    entity.setStatus(order.getStatus());
                    return entity;
                })
                .flatMap(repository::save)
                .subscribe();
    }
}
