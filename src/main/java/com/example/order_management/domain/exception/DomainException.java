package com.example.order_management.domain.exception;

/**
 * Base exception for all domain-related errors.
 * All domain exceptions must extend this class to maintain consistency.
 */
public abstract class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
