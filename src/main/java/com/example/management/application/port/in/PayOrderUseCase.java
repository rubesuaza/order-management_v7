package com.example.management.application.port.in;

import com.example.management.application.port.out.dto.OrderOutputDTO;

import java.util.UUID;

/**
 * Incoming port for processing order payment.
 */
public interface PayOrderUseCase {

    OrderOutputDTO pay(UUID orderId);
}
