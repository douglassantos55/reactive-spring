package br.com.fgto.customers.controller;

import br.com.fgto.customers.exception.ResourceNotFoundException;
import br.com.fgto.customers.repository.CustomerRepository;
import br.com.fgto.customers.entity.Customer;
import br.com.fgto.customers.message.Messenger;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private Messenger messenger;

    @GetMapping
    public List<Customer> list() {
        return repository.findByDeletedAtIsNull();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return repository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer", id));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer customer) throws JsonProcessingException {
        customer = repository.save(customer);
        messenger.saveMessage("customer.created", "notifications.exchange", customer);

        return customer;
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer data) throws JsonProcessingException {
        Customer customer = get(id);

        customer.setName(data.getName());
        customer.setDocument(data.getDocument());
        customer.setBillingAddress(data.getBillingAddress());
        customer.setDeliveryAddress(data.getDeliveryAddress());

        customer = repository.save(customer);
        messenger.saveMessage("customer.updated", "notifications.exchange", customer);

        return customer;
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Customer delete(@PathVariable Long id) throws JsonProcessingException {
        Customer customer = get(id);
        customer.setDeletedAt(Instant.now());

        repository.save(customer);
        messenger.saveMessage("customer.deleted", "notifications.exchange", customer);

        return customer;
    }
}
