package br.com.ftgo.payment.controller;

import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodsController {
    private PaymentMethodsRepository repository;

    public PaymentMethodsController(PaymentMethodsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PaymentMethod> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public PaymentMethod get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
