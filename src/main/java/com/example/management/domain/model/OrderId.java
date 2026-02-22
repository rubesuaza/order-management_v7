package com.example.management.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identifier of an Order.
 */
public final class OrderId {

    private final UUID value;

    public OrderId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        this.value = value;
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
