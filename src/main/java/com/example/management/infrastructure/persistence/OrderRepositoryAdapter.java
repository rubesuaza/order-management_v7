package com.example.management.infrastructure.persistence;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.infrastructure.persistence.jpa.OrderJpaEntity;
import com.example.management.infrastructure.persistence.jpa.OrderJpaRepository;
import com.example.management.infrastructure.persistence.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Output adapter implementing OrderRepository port.
 * Persists Order aggregate using JPA.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = jpaRepository.findByIdWithItems(order.getId().getValue())
                .map(existing -> mapper.updateJpaEntity(existing, order))
                .orElseGet(() -> mapper.toJpaEntity(order));
        OrderJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findByIdWithItems(orderId.getValue())
                .map(mapper::toDomain);
    }
}
