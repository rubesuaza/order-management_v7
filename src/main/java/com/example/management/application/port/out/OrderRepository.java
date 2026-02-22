package com.example.management.application.port.out;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;

import java.util.Optional;

/**
 * Output port for Order persistence.
 * Implemented by infrastructure adapters.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);
}
