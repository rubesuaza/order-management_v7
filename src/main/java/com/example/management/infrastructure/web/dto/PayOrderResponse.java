package com.example.management.infrastructure.web.dto;

import java.util.UUID;

public record PayOrderResponse(
        UUID orderId,
        String status
) {}
