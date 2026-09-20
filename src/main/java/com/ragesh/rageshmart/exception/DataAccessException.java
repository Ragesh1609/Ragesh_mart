package com.ragesh.rageshmart.exception;

/** Wraps SQLException so the service layer never depends on java.sql directly. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
