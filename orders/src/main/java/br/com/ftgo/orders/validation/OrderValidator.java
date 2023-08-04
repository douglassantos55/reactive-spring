package br.com.ftgo.orders.validation;

import br.com.ftgo.orders.dto.OrderDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class OrderValidator implements Validator {
    private LocalValidatorFactoryBean validator;

    public OrderValidator(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Validate using constraint annotations
        ValidationUtils.invokeValidator(validator, target, errors);

        OrderDTO order = (OrderDTO) target;

        if (order.getPaymentType().equals("credit_card")) {
            ValidationUtils.rejectIfEmpty(errors, "card", "not.null", "must not be empty");

            try {
                errors.pushNestedPath("card");
                ValidationUtils.invokeValidator(validator, order.getCard(), errors);
            } finally {
                errors.popNestedPath();
            }
        }
    }
}
