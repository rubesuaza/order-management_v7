package com.example.management.application.exception;

/**
 * Thrown when an Order is requested but cannot be found (e.g. by ID).
 * Application-layer exception for use cases that need to signal missing resources.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
