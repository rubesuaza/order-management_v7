package com.example.management.application.service;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private PayOrderService payOrderService;

    @BeforeEach
    void setUp() {
        payOrderService = new PayOrderService(orderRepository);
    }

    @Test
    void shouldMarkOrderAsPaidAndSave() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("10.00")));
        Order order = Order.create(new OrderId(orderId), UUID.randomUUID(), List.of(item));
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = payOrderService.pay(orderId);

        // Assert
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> payOrderService.pay(orderId))
                .isInstanceOf(PayOrderService.OrderNotFoundException.class)
                .hasMessageContaining("Order not found")
                .hasMessageContaining(orderId.toString());
    }

    @Test
    void shouldThrowInvalidOrderStateExceptionWhenOrderTotalBelowMinimum() {
        // Arrange - order total 5.00 < 10.00 minimum
        UUID orderId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00")));
        Order order = Order.create(new OrderId(orderId), UUID.randomUUID(), List.of(item));
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> payOrderService.pay(orderId))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("10.00");
    }

    @Test
    void shouldThrowInvalidOrderStateExceptionWhenOrderAlreadyPaid() {
        // Arrange - order already paid
        UUID orderId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("10.00")));
        Order order = Order.create(new OrderId(orderId), UUID.randomUUID(), List.of(item));
        order.markAsPaid();
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> payOrderService.pay(orderId))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("PENDING");
    }
}
