package org.example.orderservice.repository;

import org.example.orderservice.model.order.OrderLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderLongRepository extends JpaRepository<OrderLong, Integer> {
    List<OrderLong> findOrderLongsByUserId(UUID userId);
}
