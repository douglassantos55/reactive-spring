package br.com.reconcip.customers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class ValidationErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrors validationErrors(WebExchangeBindException exception) {
        ValidationErrors errors = new ValidationErrors();
        for (FieldError error : exception.getFieldErrors()) {
            errors.setError(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    private class ValidationErrors {
        private final Map<String, String> errors;

        public ValidationErrors() {
            errors = new HashMap<>();
        }

        public void setError(String key, String message) {
            errors.put(key, message);
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
