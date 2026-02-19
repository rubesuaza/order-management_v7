package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for OrderMapper (Infrastructure layer).
 * Tests mapping between domain Order aggregate and JPA entities.
 */
@DisplayName("Order Mapper Tests")
class OrderMapperTest {

    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
    }

    @Test
    @DisplayName("Should map Order to OrderEntity with all fields")
    void shouldMapOrderToEntity() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        Order order = Order.fromPersistence(
                orderId,
                customerId,
                OrderStatus.PENDING,
                createdAt,
                List.of(new OrderItem(productId, 2, new Money(new BigDecimal("10.50"), "USD")))
        );

        // Act
        OrderEntity entity = mapper.toEntity(order);

        // Assert
        assertThat(entity.getId()).isEqualTo(orderId);
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getStatus()).isEqualTo("PENDING");
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getItems()).hasSize(1);
        assertThat(entity.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(entity.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(entity.getItems().get(0).getUnitPrice()).isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    @DisplayName("Should map OrderEntity to Order (toDomain)")
    void shouldMapEntityToDomain() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        OrderEntity entity = new OrderEntity();
        entity.setId(orderId);
        entity.setCustomerId(customerId);
        entity.setStatus(OrderStatus.PAID.name());
        entity.setTotalAmount(new BigDecimal("21.00"));
        entity.setCurrency("USD");
        entity.setCreatedAt(createdAt);
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setId(UUID.randomUUID());
        itemEntity.setOrder(entity);
        itemEntity.setProductId(productId);
        itemEntity.setQuantity(2);
        itemEntity.setUnitPrice(new BigDecimal("10.50"));
        entity.setItems(List.of(itemEntity));

        // Act
        Order domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(orderId);
        assertThat(domain.getCustomerId()).isEqualTo(customerId);
        assertThat(domain.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(domain.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(domain.getTotalAmount().getCurrency()).isEqualTo("USD");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(domain.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(domain.getItems().get(0).getUnitPrice().getAmount()).isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    @DisplayName("Should round-trip Order to Entity to Order preserving data")
    void shouldRoundTripOrderToEntityToOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID product1 = UUID.randomUUID();
        UUID product2 = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        Order original = Order.fromPersistence(
                orderId,
                customerId,
                OrderStatus.SHIPPED,
                createdAt,
                List.of(
                        new OrderItem(product1, 1, new Money(new BigDecimal("25.00"), "EUR")),
                        new OrderItem(product2, 3, new Money(new BigDecimal("5.00"), "EUR"))
                )
        );

        // Act
        OrderEntity entity = mapper.toEntity(original);
        Order roundTripped = mapper.toDomain(entity);

        // Assert
        assertThat(roundTripped.getId()).isEqualTo(original.getId());
        assertThat(roundTripped.getCustomerId()).isEqualTo(original.getCustomerId());
        assertThat(roundTripped.getStatus()).isEqualTo(original.getStatus());
        assertThat(roundTripped.getTotalAmount().getAmount()).isEqualByComparingTo(original.getTotalAmount().getAmount());
        assertThat(roundTripped.getTotalAmount().getCurrency()).isEqualTo(original.getTotalAmount().getCurrency());
        assertThat(roundTripped.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(roundTripped.getItems()).hasSize(2);
    }
}
