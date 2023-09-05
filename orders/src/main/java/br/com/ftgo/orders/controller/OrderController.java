package br.com.ftgo.orders.controller;

import br.com.ftgo.orders.dto.OrderDTO;
import br.com.ftgo.orders.entity.*;
import br.com.ftgo.orders.event.Messenger;
import br.com.ftgo.orders.exception.RelationMissingException;
import br.com.ftgo.orders.exception.ResourceNotFoundException;
import br.com.ftgo.orders.exception.ValidationException;
import br.com.ftgo.orders.repository.CustomersRepository;
import br.com.ftgo.orders.repository.MessagesRepository;
import br.com.ftgo.orders.repository.OrdersRepository;
import br.com.ftgo.orders.repository.RestaurantsRepository;
import br.com.ftgo.orders.validation.OrderValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @Autowired
    private OrderValidator orderValidator;

    @Autowired
    private Messenger messenger;

    @GetMapping
    public Flux<Order> list(Order orderSearchCriteria, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int perPage) {
        return repository.findBy(Example.of((Order) orderSearchCriteria), query -> query.page(PageRequest.of(page-1, perPage)))
                .flatMapIterable(stream -> stream.toList())
                .flatMap(order ->
                        customersRepository.findById(order.getCustomerId())
                                .doOnNext(customer -> order.setCustomer(customer))
                                .thenReturn(order)
                )
                .flatMap(order ->
                        restaurantsRepository.findById(order.getRestaurantId())
                                .doOnNext(restaurant -> order.setRestaurant(restaurant))
                                .thenReturn(order)
                );
    }

    @GetMapping("/{id}")
    public Mono<Order> get(@PathVariable String id) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Order.class, id)))
                .flatMap(order ->
                        customersRepository.findById(order.getCustomerId())
                                .doOnNext(customer -> order.setCustomer(customer))
                                .thenReturn(order)
                )
                .flatMap(order ->
                        restaurantsRepository.findById(order.getRestaurantId())
                                .doOnNext(restaurant -> order.setRestaurant(restaurant))
                                .thenReturn(order)
                );
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> create(@RequestBody Mono<OrderDTO> request) {
        RelationMissingException errors = new RelationMissingException();

        return request
                .doOnNext(order -> {
                    Errors validationErrors = new BeanPropertyBindingResult(order, "order");
                    orderValidator.validate(order, validationErrors);

                    if (validationErrors.hasErrors()) {
                        throw new ValidationException(validationErrors);
                    }
                })
                .flatMap(order ->
                        customersRepository
                                .findById(order.getCustomerId())
                                .doOnNext(customer -> order.setCustomer(customer))
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Customer.class, order.getCustomerId())))
                                .doOnError(ResourceNotFoundException.class, e -> errors.addError("customerId"))
                                .onErrorComplete()
                                .thenReturn(order)
                )
                .flatMap(order ->
                        restaurantsRepository
                                .findById(order.getRestaurantId())
                                .doOnNext(restaurant -> order.setRestaurant(restaurant))
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Restaurant.class, order.getRestaurantId())))
                                .doOnError(ResourceNotFoundException.class, e -> errors.addError("restaurantId"))
                                .onErrorComplete()
                                .thenReturn(order)
                )
                .flatMap(order -> {
                    if (errors.hasErrors()) {
                        return Mono.error(errors);
                    }
                    return Mono.just(order);
                })
                .map(order -> Order.from(order))
                .flatMap(repository::save)
                .flatMap(order -> messenger.saveMessage("payment.process", "payment.exchange", order).thenReturn(order))
                .flatMap(order -> messenger.saveMessage("order.created", "notifications.exchange", order).thenReturn(order));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Mono<Order> cancel(@PathVariable String id) {
        return repository.findById(id)
                .filter(order -> !order.isCancelled())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(Order.class, id)))
                .map(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    return order;
                })
                .flatMap(repository::save)
                .flatMap(order -> messenger.saveMessage("order.cancelled", "notifications.exchange", order).thenReturn(order));
    }
}
