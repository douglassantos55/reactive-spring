package br.com.ftgo.restaurants.exception.handler;

import br.com.ftgo.restaurants.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.logging.Logger;

@RestControllerAdvice
public class NotFoundExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundException(ResourceNotFoundException exception) {
        Logger.getGlobal().info(exception.getMessage());
    }
}
