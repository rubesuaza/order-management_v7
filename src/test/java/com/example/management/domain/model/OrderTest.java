package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final Money MIN_ORDER_AMOUNT = new Money(new BigDecimal("10.00"));
    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    @Test
    void shouldCreateOrderWithAtLeastOneItem() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("5.50")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("11.00"));
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenCreatingOrderWithNoItems() {
        assertThatThrownBy(() -> Order.create(OrderId.generate(), CUSTOMER_ID, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one");
    }

    @Test
    void shouldThrowWhenCreatingOrderWithEmptyItems() {
        assertThatThrownBy(() -> Order.create(OrderId.generate(), CUSTOMER_ID, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCalculateTotalFromMultipleItems() {
        OrderItem item1 = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("3.00")));
        OrderItem item2 = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item1, item2));

        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("11.00"));
    }

    @Test
    void shouldMarkAsPaidWhenTotalMeetsMinimum() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));

        order.markAsPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowWhenMarkingAsPaidWithTotalBelowMinimum() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("10.00");
    }

    @Test
    void shouldShipOrderWhenPaid() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));
        order.markAsPaid();

        order.ship();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldThrowWhenShippingPendingOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));

        assertThatThrownBy(order::ship)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("PAID");
    }

    @Test
    void shouldCancelOrderWhenPending() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldCancelOrderWhenPaid() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));
        order.markAsPaid();

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowWhenCancellingShippedOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));
        order.markAsPaid();
        order.ship();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void shouldThrowWhenCancellingAlreadyCancelledOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("6.00")));
        Order order = Order.create(OrderId.generate(), CUSTOMER_ID, List.of(item));
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }
}
