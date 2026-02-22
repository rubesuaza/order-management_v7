package com.example.management.domain.exception;

/**
 * Thrown when an illegal state transition is attempted on an Order.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
