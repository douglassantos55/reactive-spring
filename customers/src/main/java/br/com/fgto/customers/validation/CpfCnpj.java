package br.com.fgto.customers.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE_PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpj {
    String message() default "invalid document";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
