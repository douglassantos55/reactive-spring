package br.com.ftgo.orders.dto;

import br.com.ftgo.orders.entity.OrderItem;
import br.com.ftgo.orders.validation.CardPayment;
import br.com.ftgo.orders.validation.OrderSequenceProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.group.GroupSequenceProvider;

import java.util.List;

@GroupSequenceProvider(OrderSequenceProvider.class)
public record OrderDTO(
        @NotNull
        Long customerId,

        @NotEmpty
        String restaurantId,

        @NotEmpty
        String paymentType,

        @Valid
        @NotNull(groups = CardPayment.class)
        CardInformation card,

        @Valid
        @NotEmpty
        List<OrderItem>items
) {
}
