package br.com.ftgo.orders.exception.handler;

import br.com.ftgo.orders.exception.RelationMissingException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {
    List<String> errors = new ArrayList<>();

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrors relationHandle(RelationMissingException exception) {
        return exception.getErrors();
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrors handleValidationException(WebExchangeBindException exception) {
        ValidationErrors errors = new ValidationErrors();

        for (FieldError error : exception.getFieldErrors()) {
            errors.addError(error.getField(), error.getDefaultMessage());
        }

        return errors;
    }

}
