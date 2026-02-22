package com.example.management.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderIdTest {

    @Test
    void shouldCreateOrderIdFromUuid() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);
        assertThat(orderId.getValue()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowWhenUuidIsNull() {
        assertThatThrownBy(() -> new OrderId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrderId cannot be null");
    }

    @Test
    void shouldGenerateNewOrderId() {
        OrderId orderId = OrderId.generate();
        assertThat(orderId.getValue()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        UUID uuid = UUID.randomUUID();
        OrderId id1 = new OrderId(uuid);
        OrderId id2 = new OrderId(uuid);
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
