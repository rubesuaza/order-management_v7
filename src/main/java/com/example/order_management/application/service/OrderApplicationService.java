package com.example.order_management.application.service;

import com.example.order_management.application.port.out.OrderRepository;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service (use cases) for order operations.
 * Orchestrates domain and delegates persistence to the repository port.
 */
@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order with the given customer and items.
     */
    @Transactional
    public Order createOrder(UUID customerId, List<OrderItemDto> items) {
        List<OrderItem> domainItems = items.stream()
                .map(dto -> new OrderItem(
                        dto.productId(),
                        dto.quantity(),
                        new Money(dto.unitPrice(), dto.currency() != null ? dto.currency() : "USD")
                ))
                .collect(Collectors.toList());
        Order order = new Order(customerId, domainItems);
        return orderRepository.save(order);
    }

    /**
     * Retrieves an order by id.
     */
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    /**
     * Processes payment for an order (simulated), marking it as PAID.
     */
    @Transactional
    public Order payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markAsPaid();
        return orderRepository.save(order);
    }

    public record OrderItemDto(UUID productId, int quantity, BigDecimal unitPrice, String currency) {}
}
