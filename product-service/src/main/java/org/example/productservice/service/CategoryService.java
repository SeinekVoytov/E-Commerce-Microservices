package org.example.productservice.service;

import org.example.productservice.dto.CategoryWithChildrenDto;

import java.util.Set;

public interface CategoryService {

    Set<CategoryWithChildrenDto> getRootCategories();
}