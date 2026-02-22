package com.example.management.application.port.in;

import com.example.management.domain.model.Order;

import java.util.UUID;

/**
 * Incoming port for processing order payment.
 */
public interface PayOrderUseCase {

    Order pay(UUID orderId);
}
