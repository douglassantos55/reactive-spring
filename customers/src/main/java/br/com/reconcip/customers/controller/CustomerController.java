package br.com.reconcip.customers.controller;

import br.com.reconcip.customers.entity.Customer;
import br.com.reconcip.customers.exception.ResourceNotFoundException;
import br.com.reconcip.customers.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final RabbitTemplate template;

    private final CustomerRepository repository;

    @Autowired
    public CustomerController(CustomerRepository repository, RabbitTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    @GetMapping
    public Flux<Customer> list() {
        return repository.findByDeletedAtIsNull();
    }

    @GetMapping("/{id}")
    public Mono<Customer> get(@PathVariable Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("customer", id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@Valid @RequestBody Customer customer) {
        return repository.save(customer).doOnSuccess(customer1 -> {
            Message message = new Message(new byte[]{customer1.getId().byteValue()});
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            template.send("exchange.customers", "customer.created", message);
        });
    }

    @PutMapping("/{id}")
    public Mono<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer data) {
        Mono<Customer> customer = get(id);

        return customer.flatMap(customer1 -> {
            customer1.setName(data.getName());
            customer1.setBillingAddress(data.getBillingAddress());
            customer1.setDeliveryAddress(data.getDeliveryAddress());

            return repository.save(customer1);
        });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return repository.deleteById(id);
    }
}
