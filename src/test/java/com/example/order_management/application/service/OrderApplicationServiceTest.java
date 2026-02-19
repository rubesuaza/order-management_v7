package com.example.order_management.application.service;

import com.example.order_management.application.port.out.OrderRepository;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderApplicationService (Application layer).
 * Mocks OrderRepository; only tests use case orchestration logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Application Service Tests")
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private OrderApplicationService sut;

    @BeforeEach
    void setUp() {
        sut = new OrderApplicationService(orderRepository);
    }

    @Test
    @DisplayName("Should create order and delegate save to repository")
    void shouldCreateOrderAndSave() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderApplicationService.OrderItemDto> items = List.of(
                new OrderApplicationService.OrderItemDto(productId, 2, new BigDecimal("10.00"), "USD")
        );
        Order savedOrder = new Order(customerId, List.of(
                new OrderItem(productId, 2, new Money(new BigDecimal("10.00"), "USD"))
        ));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = sut.createOrder(customerId, items);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order captured = orderCaptor.getValue();
        assertThat(captured.getCustomerId()).isEqualTo(customerId);
        assertThat(captured.getItems()).hasSize(1);
        assertThat(captured.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result).isEqualTo(savedOrder);
    }

    @Test
    @DisplayName("Should use USD when currency is null in DTO")
    void shouldUseUsdWhenCurrencyNull() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderApplicationService.OrderItemDto> items = List.of(
                new OrderApplicationService.OrderItemDto(productId, 1, new BigDecimal("5.00"), null)
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        sut.createOrder(customerId, items);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotalAmount().getCurrency()).isEqualTo("USD");
        assertThat(orderCaptor.getValue().getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("Should get order by id when exists")
    void shouldGetOrderByIdWhenExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of(
                new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("10.00")))
        ));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order result = sut.getOrder(orderId);

        // Assert
        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void shouldThrowWhenOrderNotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should pay order and save updated order")
    void shouldPayOrderAndSave() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of(
                new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("15.00")))
        ));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order result = sut.payOrder(orderId);

        // Assert
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when paying non-existent order")
    void shouldThrowWhenPayingNonExistentOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.payOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}
