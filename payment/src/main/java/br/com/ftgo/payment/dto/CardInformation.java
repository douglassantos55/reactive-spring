package br.com.ftgo.payment.dto;

public record CardInformation(String holderName, String number, String expDate, String cvv) {
}
