package br.com.ftgo.orders.exception.handler;

import java.util.Map;
import java.util.WeakHashMap;

public class ValidationErrors {
    private Map<String, String> errors = new WeakHashMap<>();

    public void addError(String field, String message) {
        String key = field.replace("[", ".");
        key = key.replace("]", "");
        errors.put(key, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
