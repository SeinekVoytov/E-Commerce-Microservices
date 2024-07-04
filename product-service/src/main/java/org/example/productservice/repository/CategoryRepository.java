package org.example.productservice.repository;

import org.example.productservice.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Set<Category> findAllByIdIn(Set<Integer> ids);

    Set<Category> findAllByParentCategoryIsNull();

    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}