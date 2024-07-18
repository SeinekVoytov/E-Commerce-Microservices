package org.example.orderservice.repository;

import org.example.orderservice.model.PickUpPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PickUpPointRepository extends JpaRepository<PickUpPoint, UUID> {
}
