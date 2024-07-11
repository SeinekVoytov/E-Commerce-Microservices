package org.example.productservice.repository;

import org.example.productservice.model.CountryManufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryManufacturerRepository extends JpaRepository<CountryManufacturer, Integer> {
    Optional<CountryManufacturer> findByName(String name);
}
