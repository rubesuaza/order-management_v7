package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for OrderItem entity following TDD approach.
 */
@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create OrderItem with valid attributes")
    void shouldCreateOrderItemWithValidAttributes() {
        // Given
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        
        // When
        OrderItem item = new OrderItem(productId, 2, unitPrice);
        
        // Then
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    @DisplayName("Should throw InvalidItemException when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Given
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        
        // When & Then
        assertThatThrownBy(() -> new OrderItem(productId, 0, unitPrice))
            .isInstanceOf(InvalidItemException.class)
            .hasMessageContaining("quantity");
    }

    @Test
    @DisplayName("Should throw InvalidItemException when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        
        // When & Then
        assertThatThrownBy(() -> new OrderItem(productId, -1, unitPrice))
            .isInstanceOf(InvalidItemException.class)
            .hasMessageContaining("quantity");
    }

    @Test
    @DisplayName("Should throw InvalidItemException when unitPrice is negative")
    void shouldThrowExceptionWhenUnitPriceIsNegative() {
        // Given
        UUID productId = UUID.randomUUID();
        Money negativePrice = new Money(new BigDecimal("-10.50"));
        
        // When & Then
        assertThatThrownBy(() -> new OrderItem(productId, 2, negativePrice))
            .isInstanceOf(InvalidItemException.class)
            .hasMessageContaining("price");
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void shouldCalculateTotalPriceCorrectly() {
        // Given
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        OrderItem item = new OrderItem(productId, 3, unitPrice);
        
        // When
        Money total = item.getTotalPrice();
        
        // Then
        assertThat(total.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        assertThat(total.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should allow zero price")
    void shouldAllowZeroPrice() {
        // Given
        UUID productId = UUID.randomUUID();
        Money zeroPrice = new Money(BigDecimal.ZERO);
        
        // When
        OrderItem item = new OrderItem(productId, 1, zeroPrice);
        
        // Then
        assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
