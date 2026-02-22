package com.example.management.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
        @NotNull(message = "productId is required")
        UUID productId,
        @Positive(message = "quantity must be positive")
        int quantity,
        @NotNull(message = "unitPrice is required")
        @DecimalMin(value = "0", inclusive = true, message = "unitPrice cannot be negative")
        BigDecimal unitPrice
) {}
