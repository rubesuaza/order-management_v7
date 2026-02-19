package com.example.order_management.application.service;

import java.util.UUID;

/**
 * Thrown when an order is not found by id.
 * Maps to HTTP 404 in the REST adapter.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }
}
