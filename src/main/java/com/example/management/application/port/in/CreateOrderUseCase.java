package com.example.management.application.port.in;

import com.example.management.application.port.out.dto.OrderOutputDTO;
import com.example.management.application.port.in.command.OrderItemInput;

import java.util.List;
import java.util.UUID;

/**
 * Incoming port for creating orders.
 */
public interface CreateOrderUseCase {

    OrderOutputDTO create(UUID customerId, List<OrderItemInput> items);
}
