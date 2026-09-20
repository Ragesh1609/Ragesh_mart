package com.ragesh.rageshmart.exception;

/** Thrown when a requested entity does not exist (maps to HTTP 404). */
public class NotFoundException extends Exception {
    public NotFoundException(String message) { super(message); }
}
