package br.com.ftgo.orders.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class resource, Object id) {
        super(String.format("Resource %s with ID %s not found", resource, id));
    }
}
