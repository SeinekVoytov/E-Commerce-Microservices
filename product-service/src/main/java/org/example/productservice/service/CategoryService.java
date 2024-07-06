package org.example.productservice.service;

import org.example.productservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CategoryService {

    Set<CategoryWithChildrenDto> getRootCategories();

    CategoryWithParentDto createCategory(RequestCategoryDto data);

    CategoryWithChildrenDto deleteCategory(String identifier);

    CategoryWithParentDto updateCategory(String identifier, UpdateCategoryDto data);

    Page<ProductDto> getProductsByCategory(String categoryIdentifier, Pageable pageable);
}