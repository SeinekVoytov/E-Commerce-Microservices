package org.example.productservice.repository;

import org.example.productservice.model.ProductLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLongRepository extends JpaRepository<ProductLong, Integer> {
}
