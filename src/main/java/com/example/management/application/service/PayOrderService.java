package com.example.management.application.service;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service for processing order payment.
 */
@Service
public class PayOrderService {

    private final OrderRepository orderRepository;

    public PayOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order pay(UUID orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.markAsPaid();
        return orderRepository.save(order);
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }
}
