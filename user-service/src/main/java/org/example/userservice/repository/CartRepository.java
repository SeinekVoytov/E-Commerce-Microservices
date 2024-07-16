package org.example.userservice.repository;

import org.example.userservice.model.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends CrudRepository<Cart, UUID> {

    Optional<Cart> findByUserId(UUID userId);
}
