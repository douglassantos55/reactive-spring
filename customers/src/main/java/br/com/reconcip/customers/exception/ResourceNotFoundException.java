package br.com.reconcip.customers.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, Long id) {
        super(String.format("Resource '%s' with ID '%d' was not found", entity, id));
    }
}
