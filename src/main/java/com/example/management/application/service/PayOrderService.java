package com.example.management.application.service;

import com.example.management.application.exception.OrderNotFoundException;
import com.example.management.application.port.in.PayOrderUseCase;
import com.example.management.application.port.out.OrderRepository;
import com.example.management.application.port.out.dto.OrderOutputDTO;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for processing order payment.
 */
@Service
public class PayOrderService implements PayOrderUseCase {

    private final OrderRepository orderRepository;

    public PayOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderOutputDTO pay(UUID orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.markAsPaid();
        Order saved = orderRepository.save(order);
        return OrderToOutputMapper.toOutputDTO(saved);
    }
}
