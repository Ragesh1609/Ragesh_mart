package com.ragesh.rageshmart.exception;

/** Thrown by the service layer when input fails validation (maps to HTTP 400). */
public class ValidationException extends Exception {
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
