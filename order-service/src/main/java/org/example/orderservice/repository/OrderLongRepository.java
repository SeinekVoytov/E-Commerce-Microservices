package org.example.orderservice.repository;

import org.example.orderservice.model.order.OrderLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderLongRepository extends JpaRepository<OrderLong, Integer> {
    Optional<OrderLong> findOrderLongByIdAndUserId(int id, UUID userId);
}
