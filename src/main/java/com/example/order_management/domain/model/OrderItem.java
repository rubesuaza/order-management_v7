package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing an item within an Order.
 * Part of the Order aggregate.
 */
public class OrderItem {
    
    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;
    
    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be strictly greater than zero");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("UnitPrice cannot be null");
        }
        if (unitPrice.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("UnitPrice cannot be negative");
        }
        
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public Money getUnitPrice() {
        return unitPrice;
    }
    
    /**
     * Calculates the total price for this item (unitPrice * quantity).
     * 
     * @return the total price as Money
     */
    public Money getTotalPrice() {
        return unitPrice.multiply(quantity);
    }
}
