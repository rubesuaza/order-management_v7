package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root representing an Order.
 * Encapsulates business rules and invariants for order management.
 */
public class Order {
    
    private final UUID id;
    private final UUID customerId;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private Money totalAmount;
    
    private static final BigDecimal MINIMUM_ORDER_VALUE = new BigDecimal("10.00");
    
    public Order(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one OrderItem");
        }
        
        // Validate currency consistency
        validateCurrencyConsistency(items);
        
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotalAmount();
    }
    
    public UUID getId() {
        return id;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * Marks the order as PAID.
     * Business rule: Order total must be at least 10.00 USD.
     * 
     * @throws InvalidOrderStateException if total amount is less than minimum
     */
    public void markAsPaid() {
        if (totalAmount.getAmount().compareTo(MINIMUM_ORDER_VALUE) < 0) {
            throw new InvalidOrderStateException(
                String.format("Order cannot be placed. Minimum order value is %s %s, but order total is %s",
                    MINIMUM_ORDER_VALUE, totalAmount.getCurrency(), totalAmount)
            );
        }
        this.status = OrderStatus.PAID;
    }
    
    /**
     * Marks the order as SHIPPED.
     * Business rule: Order must be in PAID status.
     * 
     * @throws InvalidOrderStateException if order is not in PAID status
     */
    public void markAsShipped() {
        if (this.status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                String.format("Order can only be shipped if it is in PAID status. Current status: %s", this.status)
            );
        }
        this.status = OrderStatus.SHIPPED;
    }
    
    /**
     * Marks the order as DELIVERED.
     * Business rule: Order must be in SHIPPED status.
     * 
     * @throws InvalidOrderStateException if order is not in SHIPPED status
     */
    public void markAsDelivered() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                String.format("Order can only be delivered if it is in SHIPPED status. Current status: %s", this.status)
            );
        }
        this.status = OrderStatus.DELIVERED;
    }
    
    /**
     * Cancels the order.
     * Business rule: Order can only be cancelled if it is PENDING or PAID.
     * 
     * @throws InvalidOrderStateException if order is SHIPPED or DELIVERED
     */
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                String.format("Order cannot be cancelled. A %s order cannot be cancelled.", this.status)
            );
        }
        this.status = OrderStatus.CANCELLED;
    }
    
    /**
     * Validates that all items have the same currency.
     * 
     * @param items the list of order items
     * @throws CurrencyMismatchException if items have different currencies
     */
    private void validateCurrencyConsistency(List<OrderItem> items) {
        if (items.isEmpty()) {
            return;
        }
        
        String firstCurrency = items.get(0).getUnitPrice().getCurrency();
        for (OrderItem item : items) {
            if (!item.getUnitPrice().getCurrency().equals(firstCurrency)) {
                throw new CurrencyMismatchException(
                    String.format("All order items must have the same currency. Found: %s and %s",
                        firstCurrency, item.getUnitPrice().getCurrency())
                );
            }
        }
    }
    
    /**
     * Calculates the total amount by summing all item totals.
     * 
     * @return the total amount as Money
     */
    private Money calculateTotalAmount() {
        Money total = new Money(BigDecimal.ZERO, items.get(0).getUnitPrice().getCurrency());
        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }
}
