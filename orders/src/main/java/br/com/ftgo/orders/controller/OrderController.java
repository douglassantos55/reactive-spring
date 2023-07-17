package br.com.ftgo.orders.controller;

import br.com.ftgo.orders.entity.Order;
import br.com.ftgo.orders.exception.RelationMissingException;
import br.com.ftgo.orders.repository.CustomersRepository;
import br.com.ftgo.orders.repository.OrdersRepository;
import br.com.ftgo.orders.repository.RestaurantsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrdersRepository repository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    @PostMapping
    public Mono<Order> create(@RequestBody @Valid Mono<Order> request) {
        RelationMissingException errors = new RelationMissingException();

        return request
                .delayUntil(order ->
                    customersRepository.existsById(order.getCustomerId()).doOnNext(found -> {
                        if (!found) {
                            errors.addError("customerId");
                        }
                    })
                )
                .delayUntil(order ->
                    restaurantsRepository.existsById(order.getRestaurantId()).doOnNext(found -> {
                        if (!found) {
                            errors.addError("restaurantId");
                        }
                    })
                )
                .flatMap(order -> {
                    if (errors.hasErrors()) {
                        return Mono.error(errors);
                    }
                    return Mono.just(order);
                })
                .flatMap(repository::save);
    }
}
