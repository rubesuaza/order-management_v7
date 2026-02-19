package com.example.order_management.infrastructure.persistence;

import com.example.order_management.application.port.out.OrderRepository;
import com.example.order_management.domain.model.Order;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter that implements the OrderRepository output port.
 * Bridges the application layer to JPA persistence; application depends only on the port.
 */
@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(mapper::toDomain);
    }
}
