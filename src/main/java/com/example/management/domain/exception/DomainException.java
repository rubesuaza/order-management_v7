package com.example.management.domain.exception;

/**
 * Base exception for all domain errors.
 * Domain layer remains framework-agnostic.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
