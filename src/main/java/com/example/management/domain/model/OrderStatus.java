package com.example.management.domain.model;

/**
 * Represents the lifecycle state of an Order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
