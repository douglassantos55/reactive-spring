package br.com.fgto.customers.controller;

import br.com.fgto.customers.exception.ResourceNotFoundException;
import br.com.fgto.customers.repository.CustomerRepository;
import br.com.fgto.customers.entity.Customer;
import br.com.fgto.customers.entity.Message;
import br.com.fgto.customers.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public Flux<Customer> list() {
        return repository.findByDeletedAtIsNull();
    }

    @GetMapping("/{id}")
    public Mono<Customer> get(@PathVariable Long id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("customer", id)));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@Valid @RequestBody Customer customer) {
        return repository.save(customer).flatMap(customer1 -> {
            Message message = new Message();

            message.setExchange("exchange.customers");
            message.setRoutingKey("customer.created");
            message.setBody(new byte[]{customer1.getId().byteValue()});

            return messageRepository.save(message).thenReturn(customer1);
        });
    }

    @PutMapping("/{id}")
    public Mono<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer data) {
        return get(id).map(customer -> {
            customer.setName(data.getName());
            customer.setBillingAddress(data.getBillingAddress());
            customer.setDeliveryAddress(data.getDeliveryAddress());

            return customer;
        }).flatMap(repository::save);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Customer> delete(@PathVariable Long id) {
        return get(id)
                .flatMap(customer -> {
                    customer.setDeletedAt(Instant.now());
                    return repository.save(customer);
                })
                .flatMap(customer -> {
                    Message message = new Message();

                    message.setExchange("exchange.customers");
                    message.setRoutingKey("customer.deleted");
                    message.setBody(new byte[]{customer.getId().byteValue()});

                    return messageRepository.save(message).map(result -> customer);
                });
    }
}
