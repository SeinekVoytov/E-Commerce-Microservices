package org.example.productservice.repository;

import org.example.productservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Set<Image> findAllByUrlIn(Set<String> urls);
}
