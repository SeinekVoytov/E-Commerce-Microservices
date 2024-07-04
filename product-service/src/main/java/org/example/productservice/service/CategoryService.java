package org.example.productservice.service;

import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.dto.UpdateCategoryDto;

import java.util.Set;

public interface CategoryService {

    Set<CategoryWithChildrenDto> getRootCategories();

    CategoryWithParentDto createCategory(RequestCategoryDto data);

    CategoryWithChildrenDto deleteCategory(String identifier);

    CategoryWithParentDto updateCategory(String identifier, UpdateCategoryDto data);
}