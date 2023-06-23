package br.com.reconcip.customers.controller;

import br.com.reconcip.customers.entity.Customer;
import br.com.reconcip.customers.exception.ResourceNotFoundException;
import br.com.reconcip.customers.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    @Autowired
    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
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
        return repository.save(customer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return repository.deleteById(id);
    }
}
