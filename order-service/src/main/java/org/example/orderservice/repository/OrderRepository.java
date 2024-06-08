package org.example.orderservice.repository;

import org.example.orderservice.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findAllByUserId(UUID userId, Pageable pageable);
}