package br.com.ftgo.orders.dto;

public record Invoice(String paymentUrl, String orderId, String status) {

}
