package br.com.ftgo.orders.dto;

import br.com.ftgo.orders.validation.CardPayment;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.CreditCardNumber;

@GroupSequence({ CardInformation.class, CardPayment.class })
public record CardInformation(
        @CreditCardNumber(groups = CardPayment.class)
        String number,

        @NotEmpty(groups = CardPayment.class)
        String holderName,

        @Pattern(regexp = "\\d{2}/\\d{4}", message = "Expiration date must be MM/YYYY", groups = CardPayment.class)
        @NotEmpty(groups = CardPayment.class)
        String expDate,

        @NotNull(groups = CardPayment.class)
        @Size(min = 3, max = 4, groups = CardPayment.class)
        String cvv
) {
}
