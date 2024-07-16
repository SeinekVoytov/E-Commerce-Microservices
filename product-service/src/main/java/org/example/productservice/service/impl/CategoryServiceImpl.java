package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.CategoryDto;
import org.example.productservice.elasticsearch.service.impl.CategorySearchService;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.ProductDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.dto.UpdateCategoryDto;
import org.example.productservice.exception.CategoryAlreadyExistsException;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.exception.InvalidCategorySelectorException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategorySearchService categorySearchService;

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public Set<?> getAllCategories(boolean withParents, boolean withChildren) {

        Function<Category, ?> mappingFunction = createMappingFunction(withParents, withChildren);

        return categoryRepository.findAll().stream()
                .map(mappingFunction)
                .collect(Collectors.toSet());
    }

    @Override
    public List<CategoryDto> search(String keyword) {
        List<Integer> matchedCategoryIds = categorySearchService.search(keyword);
        return categoryRepository.findAllByIdIn(matchedCategoryIds).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> reindex() {
        List<Category> allCategories = categoryRepository.findAll().stream()
                .peek(categorySearchService::update)
                .toList();

        return allCategories.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public Object getCategoryByIdentifier(String identifier,
                                          boolean withParents,
                                          boolean withChildren) {

        Category foundCategory = getCategoryByIdentifier(identifier);
        Function<Category, ?> mappingFunction = createMappingFunction(withParents, withChildren);
        return mappingFunction.apply(foundCategory);
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
        if (categoryRepository.existsByName(data.name())) {
            throw new CategoryAlreadyExistsException(data.name());
        }
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

    private Function<Category, ?> createMappingFunction(boolean withParents,
                                                        boolean withChildren) {

        if (withParents && withChildren) {
            throw new InvalidCategorySelectorException();
        }

        if (withParents) {
            return categoryMapper::toDtoWithParent;
        }

        if (withChildren) {
            return categoryMapper::toDtoWithChildren;
        }

        return categoryMapper::toDto;
    }

}
