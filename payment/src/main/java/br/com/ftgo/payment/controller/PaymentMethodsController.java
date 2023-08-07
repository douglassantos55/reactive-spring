package br.com.ftgo.payment.controller;

import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.gateway.PaymentGateway;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodsController {
    private PaymentMethodsRepository repository;

    private PaymentGateway gateway;

    public PaymentMethodsController(PaymentGateway gateway, PaymentMethodsRepository repository) {
        this.gateway = gateway;
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

    @Transactional
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        PaymentMethod method = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        gateway.deletePaymentMethod(method);
        repository.delete(method);
    }
}
