package org.example.productservice.repository;

import org.example.productservice.model.ProductShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductShortRepository extends JpaRepository<ProductShort, Integer> {
}
