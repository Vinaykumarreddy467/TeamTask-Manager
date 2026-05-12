package com.taskmanager.exception;

// Fixed: was package-private (missing public keyword) in original CustomExceptions.java
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
