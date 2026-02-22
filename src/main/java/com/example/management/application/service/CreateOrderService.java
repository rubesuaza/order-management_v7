package com.example.management.application.service;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for creating orders.
 */
@Service
public class CreateOrderService {

    private final OrderRepository orderRepository;

    public CreateOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order create(UUID customerId, List<OrderItemInput> items) {
        List<OrderItem> domainItems = items.stream()
                .map(i -> new OrderItem(i.productId(), i.quantity(), new Money(i.unitPrice())))
                .collect(Collectors.toList());
        Order order = Order.create(OrderId.generate(), customerId, domainItems);
        return orderRepository.save(order);
    }

    public record OrderItemInput(UUID productId, int quantity, java.math.BigDecimal unitPrice) {}
}
