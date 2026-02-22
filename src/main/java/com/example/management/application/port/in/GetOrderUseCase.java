package com.example.management.application.port.in;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Incoming port for retrieving order details.
 */
public interface GetOrderUseCase {

    Optional<Order> getById(UUID orderId);
}
