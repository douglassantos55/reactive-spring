package br.com.ftgo.payment.dto;

public record Customer(Long id, String name, String document, Address billingAddress) {
}
