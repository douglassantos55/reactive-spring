package br.com.ftgo.orders.validation;

import br.com.ftgo.orders.dto.OrderDTO;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class OrderSequenceProvider implements DefaultGroupSequenceProvider<OrderDTO> {
    @Override
    public List<Class<?>> getValidationGroups(OrderDTO order) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(OrderDTO.class);

        if (order != null && "cc".equals(order.getPaymentType())) {
            groups.add(CardPayment.class);
        }

        return groups;

    }
}
