package br.com.fgto.customers.controller;

import br.com.fgto.customers.exception.ResourceNotFoundException;
import br.com.fgto.customers.repository.CustomerRepository;
import br.com.fgto.customers.entity.Customer;
import br.com.fgto.customers.entity.Message;
import br.com.fgto.customers.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper mapper;

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
            try {
                Message message = new Message();

                message.setExchange("notifications.exchange");
                message.setRoutingKey("customer.created");
                message.setBody(mapper.writeValueAsBytes(customer1));

                return messageRepository.save(message).thenReturn(customer1);
            } catch (JsonProcessingException exception) {
                return Mono.error(exception);
            }
        });
    }

    @PutMapping("/{id}")
    public Mono<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer data) {
        return get(id)
                .map(customer -> {
                    customer.setName(data.getName());
                    customer.setBillingAddress(data.getBillingAddress());
                    customer.setDeliveryAddress(data.getDeliveryAddress());

                    return customer;
                })
                .flatMap(repository::save)
                .flatMap(customer -> {
                    try {
                        Message message = new Message();

                        message.setExchange("notifications.exchange");
                        message.setRoutingKey("customer.updated");
                        message.setBody(mapper.writeValueAsBytes(customer));

                        return messageRepository.save(message).thenReturn(customer);
                    } catch (JsonProcessingException exception) {
                        return Mono.error(exception);
                    }
                });
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
                    try {
                        Message message = new Message();

                        message.setExchange("notifications.exchange");
                        message.setRoutingKey("customer.deleted");
                        message.setBody(mapper.writeValueAsBytes(customer));

                        return messageRepository.save(message).thenReturn(customer);
                    } catch (JsonProcessingException exception) {
                        return Mono.error(exception);
                    }
                });
    }
}
