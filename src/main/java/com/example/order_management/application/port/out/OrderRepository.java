package com.example.order_management.application.port.out;

import com.example.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for persisting and retrieving Order aggregates.
 * Implemented by the infrastructure layer.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
