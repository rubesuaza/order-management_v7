package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for Order Aggregate Root following TDD approach.
 */
@DisplayName("Order Aggregate Root Tests")
class OrderTest {

    @Test
    @DisplayName("Should create Order with valid items")
    void shouldCreateOrderWithValidItems() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.50"));
        OrderItem item = new OrderItem(productId, 2, unitPrice);
        List<OrderItem> items = List.of(item);
        
        // When
        Order order = new Order(customerId, items);
        
        // Then
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("21.00"));
    }

    @Test
    @DisplayName("Should throw exception when creating Order with empty items list")
    void shouldThrowExceptionWhenCreatingOrderWithEmptyItems() {
        // Given
        UUID customerId = UUID.randomUUID();
        List<OrderItem> emptyItems = new ArrayList<>();
        
        // When & Then
        assertThatThrownBy(() -> new Order(customerId, emptyItems))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("at least one");
    }

    @Test
    @DisplayName("Should calculate total amount correctly for multiple items")
    void shouldCalculateTotalAmountCorrectly() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Money price1 = new Money(new BigDecimal("10.00"));
        Money price2 = new Money(new BigDecimal("5.00"));
        List<OrderItem> items = List.of(
            new OrderItem(productId1, 2, price1),
            new OrderItem(productId2, 3, price2)
        );
        
        // When
        Order order = new Order(customerId, items);
        
        // Then
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
    }

    @Test
    @DisplayName("Should throw CurrencyMismatchException when items have different currencies")
    void shouldThrowExceptionWhenItemsHaveDifferentCurrencies() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Money priceUSD = new Money(new BigDecimal("10.00"), "USD");
        Money priceEUR = new Money(new BigDecimal("5.00"), "EUR");
        List<OrderItem> items = List.of(
            new OrderItem(productId1, 1, priceUSD),
            new OrderItem(productId2, 1, priceEUR)
        );
        
        // When & Then
        assertThatThrownBy(() -> new Order(customerId, items))
            .isInstanceOf(CurrencyMismatchException.class)
            .hasMessageContaining("currency");
    }

    @Test
    @DisplayName("Should allow transition from PENDING to PAID when total is >= 10.00")
    void shouldAllowTransitionToPaidWhenTotalIsAtLeastTen() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // When
        order.markAsPaid();
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("Should throw exception when trying to mark as PAID with total < 10.00")
    void shouldThrowExceptionWhenMarkingAsPaidWithTotalLessThanTen() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("9.99"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // When & Then
        assertThatThrownBy(() -> order.markAsPaid())
            .isInstanceOf(InvalidOrderStateException.class)
            .hasMessageContaining("minimum");
    }

    @Test
    @DisplayName("Should allow transition from PAID to SHIPPED")
    void shouldAllowTransitionFromPaidToShipped() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        order.markAsPaid();
        
        // When
        order.markAsShipped();
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("Should throw exception when trying to ship from PENDING status")
    void shouldThrowExceptionWhenShippingFromPending() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // When & Then
        assertThatThrownBy(() -> order.markAsShipped())
            .isInstanceOf(InvalidOrderStateException.class)
            .hasMessageContaining("PAID");
    }

    @Test
    @DisplayName("Should allow cancellation from PENDING status")
    void shouldAllowCancellationFromPending() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // When
        order.cancel();
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should allow cancellation from PAID status")
    void shouldAllowCancellationFromPaid() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        order.markAsPaid();
        
        // When
        order.cancel();
        
        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when trying to cancel SHIPPED order")
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        order.markAsPaid();
        order.markAsShipped();
        
        // When & Then
        assertThatThrownBy(() -> order.cancel())
            .isInstanceOf(InvalidOrderStateException.class)
            .hasMessageContaining("SHIPPED");
    }

    @Test
    @DisplayName("Should have createdAt timestamp when created")
    void shouldHaveCreatedAtTimestamp() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        // When
        Order order = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // Then
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should have unique OrderId")
    void shouldHaveUniqueOrderId() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        // When
        Order order1 = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        Order order2 = new Order(customerId, List.of(new OrderItem(productId, 1, unitPrice)));
        
        // Then
        assertThat(order1.getId()).isNotEqualTo(order2.getId());
    }
}
