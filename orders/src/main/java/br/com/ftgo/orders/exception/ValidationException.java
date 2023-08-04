package br.com.ftgo.orders.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;

public class ValidationException extends RuntimeException {
    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors.getFieldErrors();
    }
}
