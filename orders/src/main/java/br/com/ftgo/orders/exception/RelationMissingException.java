package br.com.ftgo.orders.exception;

import br.com.ftgo.orders.exception.handler.ValidationErrors;

public class RelationMissingException extends RuntimeException {
    private ValidationErrors errors;

    public RelationMissingException() {
        errors = new ValidationErrors();
    }

    public void addError(String field) {
        errors.addError(field, "does not exist");
    }

    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public ValidationErrors getErrors() {
        return errors;
    }
}
