package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    void shouldCreateValidOrderItem() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("9.99"));
        OrderItem item = new OrderItem(productId, 2, unitPrice);

        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(item.getLineTotal().getAmount()).isEqualByComparingTo(new BigDecimal("19.98"));
    }

    @Test
    void shouldThrowWhenQuantityIsZero() {
        Money unitPrice = new Money(new BigDecimal("10.00"));
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), 0, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldThrowWhenQuantityIsNegative() {
        Money unitPrice = new Money(new BigDecimal("10.00"));
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), -1, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldThrowWhenUnitPriceIsNegative() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("-5.00"))))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("price");
    }

    @Test
    void shouldAcceptZeroUnitPrice() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 1, new Money(BigDecimal.ZERO));
        assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateLineTotalCorrectly() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 3, new Money(new BigDecimal("2.50")));
        assertThat(item.getLineTotal().getAmount()).isEqualByComparingTo(new BigDecimal("7.50"));
    }
}
