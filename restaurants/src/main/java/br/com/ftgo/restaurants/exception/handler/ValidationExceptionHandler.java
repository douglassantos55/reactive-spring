package br.com.ftgo.restaurants.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.WeakHashMap;

@RestControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrors handleValidationErrors(WebExchangeBindException exception) {
        ValidationErrors errors = new ValidationErrors();

        for (FieldError error : exception.getFieldErrors()) {
            errors.addError(error.getField(), error.getDefaultMessage());
        }

        return errors;
    }

    private class ValidationErrors {
        private Map<String, String> errors;

        public ValidationErrors() {
            errors = new WeakHashMap<>();
        }

        public void addError(String field, String message) {
            String key = field.replace("[", ".");
            key = key.replace("]", "");
            errors.put(key, message);
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
