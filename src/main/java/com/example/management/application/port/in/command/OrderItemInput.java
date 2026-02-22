package com.example.management.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-specific input DTO for order item creation.
 * Decouples the application layer from adapter-specific types.
 */
public record OrderItemInput(UUID productId, int quantity, BigDecimal unitPrice) {}
