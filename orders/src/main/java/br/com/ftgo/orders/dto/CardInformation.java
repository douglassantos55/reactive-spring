package br.com.ftgo.orders.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.CreditCardNumber;

public record CardInformation(
        @CreditCardNumber
        String number,

        @NotEmpty
        String holderName,

        @Pattern(regexp = "\\d{2}/\\d{4}", message = "Expiration date must be MM/YYYY")
        @NotEmpty()
        String expDate,

        @NotNull()
        @Size(min = 3, max = 4)
        String cvv
) {
}
