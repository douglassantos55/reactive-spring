package br.com.ftgo.orders.event.handler;

import br.com.ftgo.orders.entity.Restaurant;
import br.com.ftgo.orders.repository.RestaurantsRepository;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEventsHandler {
    @Autowired
    private RestaurantsRepository repository;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "orders.restaurant.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "restaurant.created"
            ),
            @QueueBinding(
                    value = @Queue(value = "orders.restaurant.created_updated", durable = "true"),
                    exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
                    key = "restaurant.updated"
            ),
    })
    public void handleCreatedUpdated(Restaurant restaurant) {
        repository.save(restaurant).subscribe();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "orders.restaurant.deleted", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.deleted"
    ))
    public void handleDeleted(Restaurant restaurant) {
        repository.delete(restaurant).subscribe();
    }
}
