package br.com.ftgo.payment.controller;

import br.com.ftgo.payment.entity.PaymentMethod;
import br.com.ftgo.payment.repository.PaymentMethodsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
