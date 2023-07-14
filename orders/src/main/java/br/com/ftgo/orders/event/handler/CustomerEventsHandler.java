package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.entity.Customer;
import br.com.ftgo.orders.repository.CustomersRepository;
import org.springframework.amqp.core.ExchangeTypes;
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
    public void handleCreated(Customer customer) {
        repository.save(customer).subscribe();
    }
}
