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
public class RestaurantCreatedHandler {
    @Autowired
    private RestaurantsRepository repository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "orders.restaurant.created", durable = "true"),
            exchange = @Exchange(name = "notifications.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.created"
    ))
    public void saveRestaurant(Restaurant restaurant) {
        repository.save(restaurant).subscribe();
    }
}
