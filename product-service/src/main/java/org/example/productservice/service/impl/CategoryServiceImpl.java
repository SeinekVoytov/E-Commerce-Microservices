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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public Page<ProductDto> getProductsByCategory(String identifier, Pageable pageable) {

        Category category = getCategoryByIdentifier(identifier);
        List<Product> categoryProducts = new ArrayList<>();
        retrieveAllProductsByCategory(categoryProducts, category);
        long offset = pageable.getOffset();
        long limit = pageable.getPageSize();

        return new PageImpl<>(
                categoryProducts.stream()
                        .skip(offset)
                        .limit(limit)
                        .sorted(getComparatorBySort(pageable.getSort()))
                        .map(productMapper::toDto)
                        .toList(),
                pageable,
                categoryProducts.size()
        );
    }

    private Comparator<Product> getComparatorBySort(Sort sort) {

        Comparator<Product> comparator = (p1, p2) -> 0;           // empty comparator
        Iterator<Sort.Order> orderIterator = sort.stream().iterator();

        if (orderIterator.hasNext()) {
            comparator = getComparatorByOrder(orderIterator.next());

            while (orderIterator.hasNext()) {
                comparator.thenComparing(getComparatorByOrder(orderIterator.next()));
            }
        }

        return comparator;
    }

    private Comparator<Product> getComparatorByOrder(Sort.Order order) {
        String property = order.getProperty();
        Comparator<Product> comparator = ProductServiceImpl.AVAILABLE_SORT_PARAMETERS.get(property);
        return (order.isDescending()) ? comparator.reversed() : comparator;
    }

    private void retrieveAllProductsByCategory(List<Product> accumulator, Category category) {

        Set<Category> childCategories = category.getChildCategories();
        if (!childCategories.isEmpty()) {
            for (Category child : childCategories) {
                retrieveAllProductsByCategory(accumulator, child);
            }
        }

        accumulator.addAll(category.getProducts());
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
