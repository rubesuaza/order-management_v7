package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Maps between domain Order aggregate and persistence OrderEntity.
 */
@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus().name());
        entity.setTotalAmount(order.getTotalAmount().getAmount());
        entity.setCurrency(order.getTotalAmount().getCurrency());
        entity.setCreatedAt(order.getCreatedAt());

        List<OrderItemEntity> itemEntities = new ArrayList<>(order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList());
        entity.setItems(itemEntities);
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .toList();
        return Order.fromPersistence(
                entity.getId(),
                entity.getCustomerId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                items
        );
    }

    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity order) {
        OrderItemEntity e = new OrderItemEntity();
        e.setId(UUID.randomUUID());
        e.setOrder(order);
        e.setProductId(item.getProductId());
        e.setQuantity(item.getQuantity());
        e.setUnitPrice(item.getUnitPrice().getAmount());
        return e;
    }

    private OrderItem toDomainItem(OrderItemEntity e) {
        return new OrderItem(
                e.getProductId(),
                e.getQuantity(),
                new Money(e.getUnitPrice(), e.getOrder().getCurrency())
        );
    }
}
