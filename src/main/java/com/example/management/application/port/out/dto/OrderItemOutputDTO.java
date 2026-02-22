package com.example.management.application.port.out.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-specific output DTO for order items.
 * Decouples the application layer from domain and infrastructure.
 */
public record OrderItemOutputDTO(UUID productId, int quantity, BigDecimal unitPrice) {}
