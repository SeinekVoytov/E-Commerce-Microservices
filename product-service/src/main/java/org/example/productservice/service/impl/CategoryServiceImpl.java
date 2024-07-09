package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.*;
import org.example.productservice.exception.CategoryAlreadyExistsException;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.model.Category;
import org.example.productservice.model.Product;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.service.CategoryService;
import org.example.productservice.service.ProductService;
import org.example.productservice.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

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

        Category createdCategory = Category.builder()
                .parentCategory(parent)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .name(data.name())
                .build();

        createdCategory = categoryRepository.save(createdCategory);

        return categoryMapper.toDtoWithParent(createdCategory);
    }

    @Override
    public CategoryWithParentDto updateCategory(String identifier, UpdateCategoryDto data) {
        Category categoryToBeUpdated = getCategoryByIdentifier(identifier);
        categoryToBeUpdated.setName(data.name());
        categoryToBeUpdated = categoryRepository.save(categoryToBeUpdated);
        return categoryMapper.toDtoWithParent(categoryToBeUpdated);
    }

    @Override
    public CategoryWithChildrenDto deleteCategory(String identifier) {
        Category categoryToBeDeleted = getCategoryByIdentifier(identifier);
        categoryRepository.delete(categoryToBeDeleted);
        return categoryMapper.toDtoWithChildren(categoryToBeDeleted);
    }

    @Override
    public Page<ProductDto> getProductsByCategory(String identifier,
                                                  Pageable pageable,
                                                  Predicate<Product> filter) {

        Category category = getCategoryByIdentifier(identifier);
        List<Product> categoryProducts = retrieveAllProductsByCategory(category);

        Comparator<Product> cmp = ProductService.getComparatorBySort(pageable.getSort());
        Function<Product, ProductDto> mapper = productMapper::toDto;

        return PaginationUtils.collectionToPageWithSortAndFilter(
                categoryProducts, pageable, mapper, filter, cmp
        );
    }

    private List<Product> retrieveAllProductsByCategory(Category category) {
        List<Product> products = new ArrayList<>();

        Set<Category> childCategories = category.getChildCategories();
        if (!childCategories.isEmpty()) {
            for (Category child : childCategories) {
                products.addAll(retrieveAllProductsByCategory(child));
            }
        }

        return products;
    }

    private Category getCategoryByIdentifier(String identifier) {
        try {
            Integer id = Integer.parseInt(identifier);
            return categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));
        } catch (NumberFormatException exc) {
            return categoryRepository.findByName(identifier)
                    .orElseThrow(() -> new CategoryNotFoundException(identifier));
        }
    }
}
