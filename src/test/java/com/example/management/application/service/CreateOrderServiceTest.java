package com.example.management.application.service;

import com.example.management.application.port.in.command.OrderItemInput;
import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository);
    }

    @Test
    void shouldCreateOrderAndSaveToRepository() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItemInput> items = List.of(
                new OrderItemInput(productId, 2, new BigDecimal("10.00"))
        );

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = createOrderService.create(customerId, items);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(savedOrder.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(savedOrder.getItems().get(0).getUnitPrice().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(savedOrder.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(savedOrder.getStatus()).isEqualTo(com.example.management.domain.model.OrderStatus.PENDING);
        assertThat(result.orderId()).isEqualTo(savedOrder.getId().getValue());
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void shouldCreateOrderWithMultipleItems() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        List<OrderItemInput> items = List.of(
                new OrderItemInput(productId1, 2, new BigDecimal("5.00")),
                new OrderItemInput(productId2, 1, new BigDecimal("10.00"))
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = createOrderService.create(customerId, items);

        // Assert
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void shouldGenerateNewOrderIdWhenCreating() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        List<OrderItemInput> items = List.of(
                new OrderItemInput(UUID.randomUUID(), 1, new BigDecimal("15.00"))
        );

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        createOrderService.create(customerId, items);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getId()).isNotNull();
    }
}
