package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.exception.CategoryAlreadyExistsException;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.model.Category;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Override
    public CategoryWithParentDto createCategory(RequestCategoryDto data) {

        if (categoryRepository.existsByName(data.name())) {
            throw new CategoryAlreadyExistsException(data.name());
        }

        Category parent = null;
        if (data.parentId() != null) {
            parent = categoryRepository.findById(data.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(data.parentId()));
        }

        Category createdCategory = new Category(
                null,
                parent,
                Collections.emptySet(),
                Collections.emptySet(),
                data.name()
        );

        createdCategory = categoryRepository.save(createdCategory);

        return categoryMapper.toDtoWithParent(createdCategory);
    }
}
