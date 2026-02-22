package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root for the Order Management context.
 * Encapsulates order lifecycle and business invariants.
 */
public final class Order {

    private static final Money MINIMUM_ORDER_AMOUNT = new Money(new java.math.BigDecimal("10.00"));

    private final OrderId id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private final Money totalAmount;

    private Order(OrderId id, UUID customerId, List<OrderItem> items, LocalDateTime createdAt) {
        if (id == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one OrderItem");
        }
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotal(items);
    }

    public static Order create(OrderId id, UUID customerId, List<OrderItem> items) {
        return new Order(id, customerId, items, LocalDateTime.now());
    }

    private static Money calculateTotal(List<OrderItem> items) {
        Money total = null;
        for (OrderItem item : items) {
            Money lineTotal = item.getLineTotal();
            if (total == null) {
                total = lineTotal;
            } else {
                total = total.add(lineTotal);
            }
        }
        return total;
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Order can only be marked as PAID when in PENDING status. Current: " + status);
        }
        if (!totalAmount.isGreaterThanOrEqual(MINIMUM_ORDER_AMOUNT)) {
            throw new InvalidOrderStateException(
                    "Order total must be at least 10.00 USD to be placed. Current total: " + totalAmount.getAmount());
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    "Order can only be SHIPPED when in PAID status. Current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when already SHIPPED or DELIVERED. Current: " + status);
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already CANCELLED");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
