package com.example.management.application.port.in;

import com.example.management.domain.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Incoming port for creating orders.
 */
public interface CreateOrderUseCase {

    Order create(UUID customerId, List<OrderItemInput> items);

    record OrderItemInput(UUID productId, int quantity, BigDecimal unitPrice) {}
}
