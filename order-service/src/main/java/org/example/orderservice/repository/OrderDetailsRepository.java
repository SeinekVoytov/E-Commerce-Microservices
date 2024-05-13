package org.example.orderservice.repository;

import org.example.orderservice.model.order.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {

    @Query(
            value = "SELECT * FROM order_details WHERE id = :id AND order_id IN (SELECT id FROM \"order\" WHERE user_id = :userId)",
            nativeQuery = true
    )
    Optional<OrderDetails> findOrderLongByIdAndUserId(int id, UUID userId);
}