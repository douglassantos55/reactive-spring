package br.com.reconcip.customers.controller;

import br.com.reconcip.customers.entity.Customer;
import br.com.reconcip.customers.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    @Autowired
    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@Valid @RequestBody Customer customer) {
        return repository.save(customer);
    }
}
