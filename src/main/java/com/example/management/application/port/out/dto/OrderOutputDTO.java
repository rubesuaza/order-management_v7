package com.example.management.application.port.out.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application-specific output DTO for orders.
 * Decouples the application layer from domain and infrastructure.
 */
public record OrderOutputDTO(
        UUID orderId,
        UUID customerId,
        String status,
        List<OrderItemOutputDTO> items,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt
) {}
