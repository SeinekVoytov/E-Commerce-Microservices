package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public Set<CategoryWithChildrenDto> getRootCategories() {
        return categoryRepository.findAllByParentCategoryIsNull().stream()
                .map(categoryMapper::toDtoWithChildren)
                .collect(Collectors.toSet());
    }
}
