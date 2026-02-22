package com.example.management.application.service;

import com.example.management.application.port.in.GetOrderUseCase;
import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service for retrieving order details.
 */
@Service
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(UUID orderId) {
        return orderRepository.findById(new OrderId(orderId));
    }
}
