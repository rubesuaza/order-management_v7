package com.example.management.application.service;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private GetOrderService getOrderService;

    @BeforeEach
    void setUp() {
        getOrderService = new GetOrderService(orderRepository);
    }

    @Test
    void shouldReturnOrderWhenFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("10.00")));
        Order order = Order.create(new OrderId(orderId), UUID.randomUUID(), List.of(item));
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = getOrderService.getById(orderId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId().getValue()).isEqualTo(orderId);
        verify(orderRepository).findById(new OrderId(orderId));
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(new OrderId(orderId))).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = getOrderService.getById(orderId);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository).findById(new OrderId(orderId));
    }
}
