package com.example.management.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for OrderJpaEntity.
 */
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
