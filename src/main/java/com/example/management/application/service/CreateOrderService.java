package com.example.management.application.service;

import com.example.management.application.port.in.CreateOrderUseCase;
import com.example.management.application.port.in.command.OrderItemInput;
import com.example.management.application.port.out.OrderRepository;
import com.example.management.application.port.out.dto.OrderOutputDTO;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for creating orders.
 */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    public CreateOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderOutputDTO create(UUID customerId, List<OrderItemInput> items) {
        List<OrderItem> domainItems = items.stream()
                .map(i -> new OrderItem(i.productId(), i.quantity(), new Money(i.unitPrice())))
                .toList();
        Order order = Order.create(OrderId.generate(), customerId, domainItems);
        Order saved = orderRepository.save(order);
        return OrderToOutputMapper.toOutputDTO(saved);
    }
}
