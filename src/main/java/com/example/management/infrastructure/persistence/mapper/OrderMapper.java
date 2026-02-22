package com.example.management.infrastructure.persistence.mapper;

import com.example.management.domain.model.*;
import com.example.management.infrastructure.persistence.jpa.OrderItemJpaEntity;
import com.example.management.infrastructure.persistence.jpa.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                .toList();
        entity.setItems(itemEntities);
        return entity;
    }

    /**
     * Updates the JPA entity from domain state.
     * Uses a merge strategy: matches existing items by business key (productId, quantity, unitPrice),
     * updates them in place to preserve persistence identity, adds new items, and removes orphans.
     */
    public OrderJpaEntity updateJpaEntity(OrderJpaEntity entity, Order order) {
        entity.setStatus(order.getStatus().name());
        entity.setTotalAmount(order.getTotalAmount().getAmount());
        entity.setCurrency(order.getTotalAmount().getCurrency());

        List<OrderItemJpaEntity> existingItems = new ArrayList<>(entity.getItems());
        List<OrderItem> domainItems = new ArrayList<>(order.getItems());

        // Match and update: for each existing JPA item, find a matching domain item and update in place
        for (OrderItemJpaEntity jpaItem : existingItems) {
            int matchIdx = findMatchingDomainItemIndex(domainItems, jpaItem);
            if (matchIdx >= 0) {
                OrderItem domainItem = domainItems.get(matchIdx);
                jpaItem.setProductId(domainItem.getProductId());
                jpaItem.setQuantity(domainItem.getQuantity());
                jpaItem.setUnitPrice(domainItem.getUnitPrice().getAmount());
                domainItems.remove(matchIdx); // consume so we don't create duplicate
            } else {
                entity.getItems().remove(jpaItem); // orphan removal will delete
            }
        }

        // Add new JPA entities for remaining domain items (no matching existing entity)
        for (OrderItem domainItem : domainItems) {
            OrderItemJpaEntity newItem = new OrderItemJpaEntity(
                    UUID.randomUUID(),
                    entity,
                    domainItem.getProductId(),
                    domainItem.getQuantity(),
                    domainItem.getUnitPrice().getAmount()
            );
            entity.getItems().add(newItem);
        }
        return entity;
    }

    private int findMatchingDomainItemIndex(List<OrderItem> domainItems, OrderItemJpaEntity jpaItem) {
        for (int i = 0; i < domainItems.size(); i++) {
            OrderItem d = domainItems.get(i);
            if (d.getProductId().equals(jpaItem.getProductId())
                    && d.getQuantity() == jpaItem.getQuantity()
                    && d.getUnitPrice().getAmount().compareTo(jpaItem.getUnitPrice()) == 0) {
                return i;
            }
        }
        return -1;
    }

    public Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        new Money(item.getUnitPrice(), entity.getCurrency())
                ))
                .toList();
        return Order.reconstitute(
                new OrderId(entity.getId()),
                entity.getCustomerId(),
                domainItems,
                entity.getCreatedAt(),
                OrderStatus.valueOf(entity.getStatus())
        );
    }
}
