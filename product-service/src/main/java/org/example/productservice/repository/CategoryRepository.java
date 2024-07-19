package org.example.productservice.repository;

import org.example.productservice.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    List<Category> findAllByIdIn(Collection<Integer> ids);

    List<Category> findAll();

    List<Category> findAllByParentCategoryIsNull();

    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}