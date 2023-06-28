package br.com.reconcip.customers.exception.handler;

import br.com.reconcip.customers.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.logging.Logger;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity notFoundHandler(ResourceNotFoundException exception) {
        Logger.getGlobal().info(exception.getMessage());
        return ResponseEntity.notFound().build();
    }
}
