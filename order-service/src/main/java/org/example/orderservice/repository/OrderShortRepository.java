package org.example.orderservice.repository;

import org.example.orderservice.model.order.OrderShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderShortRepository extends JpaRepository<OrderShort, Integer> {
    List<OrderShort> findOrderShortsByUserId(UUID userId);
}
