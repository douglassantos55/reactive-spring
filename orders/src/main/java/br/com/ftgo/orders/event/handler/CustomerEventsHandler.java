package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.entity.Customer;
import br.com.ftgo.orders.event.ContextHandler;
import br.com.ftgo.orders.repository.CustomersRepository;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventsHandler {
    @Autowired
    private CustomersRepository repository;

    @Autowired
    private ContextHandler contextHandler;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "orders.customer.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "customer.created"
            ),
            @QueueBinding(
                    value = @Queue(value = "orders.customer.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "customer.updated"
            )
    })
    public void handleCreated(Customer customer, Message message) {
        contextHandler.withMessageContext(message, span ->
            repository
                    .save(customer)
                    .doOnError(exception -> span.recordException(exception))
                    .block()
        );
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "orders.customer.deleted", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "customer.deleted"
    ))
    public void handleDeleted(Customer customer, Message message) {
        contextHandler.withMessageContext(message, span ->
                repository
                        .delete(customer)
                        .doOnError(exception -> span.recordException(exception))
                        .block()
        );
    }
}
