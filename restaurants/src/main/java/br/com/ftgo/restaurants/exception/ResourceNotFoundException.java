package br.com.ftgo.restaurants.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class resource, String id) {
        super(String.format("Resource '%s' with ID '%s' not found", resource, id));
    }
}
