package com.example.management.application.port.in;

import com.example.management.application.port.out.dto.OrderOutputDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Incoming port for retrieving order details.
 */
public interface GetOrderUseCase {

    Optional<OrderOutputDTO> getById(UUID orderId);
}
