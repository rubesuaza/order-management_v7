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
        this(id, customerId, items, createdAt, OrderStatus.PENDING);
    }

    private Order(OrderId id, UUID customerId, List<OrderItem> items, LocalDateTime createdAt, OrderStatus status) {
        if (id == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one OrderItem");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.status = status;
        this.totalAmount = calculateTotal(items);
    }

    public static Order create(OrderId id, UUID customerId, List<OrderItem> items) {
        return new Order(id, customerId, items, LocalDateTime.now());
    }

    /**
     * Reconstitutes an Order from persistence. Used when loading from database.
     */
    public static Order reconstitute(OrderId id, UUID customerId, List<OrderItem> items,
                                    LocalDateTime createdAt, OrderStatus status) {
        return new Order(id, customerId, items, createdAt, status);
    }

    private static Money calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(Money::add)
                .orElseThrow(() -> new IllegalStateException("Items list cannot be empty"));
    }

    public void markAsPaid() {
        if (!canBeMarkedAsPaid()) {
            throw new InvalidOrderStateException(
                    "Order can only be marked as PAID when in PENDING status. Current: " + status);
        }
        if (!meetsMinimumOrderAmount()) {
            throw new InvalidOrderStateException(
                    "Order total must be at least 10.00 USD to be placed. Current total: " + totalAmount.getAmount());
        }
        this.status = OrderStatus.PAID;
    }

    private boolean canBeMarkedAsPaid() {
        return status == OrderStatus.PENDING;
    }

    private boolean meetsMinimumOrderAmount() {
        return totalAmount.isGreaterThanOrEqual(MINIMUM_ORDER_AMOUNT);
    }

    public void ship() {
        if (!canBeShipped()) {
            throw new InvalidOrderStateException(
                    "Order can only be SHIPPED when in PAID status. Current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    private boolean canBeShipped() {
        return status == OrderStatus.PAID;
    }

    public void cancel() {
        if (isAlreadyShippedOrDelivered()) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when already SHIPPED or DELIVERED. Current: " + status);
        }
        if (isAlreadyCancelled()) {
            throw new InvalidOrderStateException("Order is already CANCELLED");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private boolean isAlreadyShippedOrDelivered() {
        return status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED;
    }

    private boolean isAlreadyCancelled() {
        return status == OrderStatus.CANCELLED;
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
