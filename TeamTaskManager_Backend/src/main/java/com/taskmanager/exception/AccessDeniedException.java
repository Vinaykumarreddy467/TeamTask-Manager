package com.taskmanager.exception;

// Fixed: was package-private (missing public keyword) in original CustomExceptions.java
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
