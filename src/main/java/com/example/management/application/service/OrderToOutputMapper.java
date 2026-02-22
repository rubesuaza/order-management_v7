package com.example.management.application.service;

import com.example.management.application.port.out.dto.OrderItemOutputDTO;
import com.example.management.application.port.out.dto.OrderOutputDTO;
import com.example.management.domain.model.Order;

import java.util.stream.Collectors;

/**
 * Maps domain Order to application output DTO.
 * Keeps mapping logic within the application layer.
 */
final class OrderToOutputMapper {

    private OrderToOutputMapper() {}

    static OrderOutputDTO toOutputDTO(Order order) {
        var items = order.getItems().stream()
                .map(item -> new OrderItemOutputDTO(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .collect(Collectors.toList());
        return new OrderOutputDTO(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getStatus().name(),
                items,
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
    }
}
