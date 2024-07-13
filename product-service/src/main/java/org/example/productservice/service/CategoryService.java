package org.example.productservice.service;

import org.example.productservice.dto.*;
import org.example.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.function.Predicate;

public interface CategoryService {

    Set<?> getAllCategories(boolean withParents, boolean withChildren);

    CategoryWithParentDto createCategory(RequestCategoryDto data);

    CategoryWithChildrenDto deleteCategory(String identifier);

    CategoryWithParentDto updateCategory(String identifier, UpdateCategoryDto data);

    Page<ProductDto> getProductsByCategory(String categoryIdentifier,
                                           Pageable pageable,
                                           Predicate<Product> filter);
}