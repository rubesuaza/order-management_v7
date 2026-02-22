package com.example.management.infrastructure.persistence.mapper;

import com.example.management.domain.model.*;
import com.example.management.infrastructure.persistence.jpa.OrderItemJpaEntity;
import com.example.management.infrastructure.persistence.jpa.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps between Domain Order/OrderItem and JPA entities.
 */
@Component
public class OrderMapper {

    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
        List<OrderItemJpaEntity> itemEntities = order.getItems().stream()
                .map(item -> new OrderItemJpaEntity(
                        UUID.randomUUID(),
                        entity,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .collect(Collectors.toList());
        entity.setItems(itemEntities);
        return entity;
    }

    public OrderJpaEntity updateJpaEntity(OrderJpaEntity entity, Order order) {
        entity.setStatus(order.getStatus().name());
        entity.setTotalAmount(order.getTotalAmount().getAmount());
        entity.setCurrency(order.getTotalAmount().getCurrency());
        entity.getItems().clear();
        List<OrderItemJpaEntity> itemEntities = order.getItems().stream()
                .map(item -> new OrderItemJpaEntity(
                        UUID.randomUUID(),
                        entity,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .collect(Collectors.toList());
        entity.getItems().addAll(itemEntities);
        return entity;
    }

    public Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        new Money(item.getUnitPrice(), entity.getCurrency())
                ))
                .collect(Collectors.toList());
        return Order.reconstitute(
                new OrderId(entity.getId()),
                entity.getCustomerId(),
                domainItems,
                entity.getCreatedAt(),
                OrderStatus.valueOf(entity.getStatus())
        );
    }
}
