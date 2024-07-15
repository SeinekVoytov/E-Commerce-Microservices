package org.example.productservice.service.impl;

import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.dto.UpdateCategoryDto;
import org.example.productservice.exception.CategoryAlreadyExistsException;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.mapper.CategoryMapper;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.model.Category;
import org.example.productservice.model.Product;
import org.example.productservice.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_ShouldThrowCategoryAlreadyExistsExc_WhenCategoryAlreadyExists() {

        var request = new RequestCategoryDto(null, "category");
        when(categoryRepository.existsByName(request.name()))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(request)
        );

        verify(categoryRepository).existsByName(request.name());
    }

    @Test
     void createCategory_ShouldThrowCategoryNotFoundExc_WhenParentCategoryNotFoundById() {

        var request = new RequestCategoryDto(1, "category");
        when(categoryRepository.existsByName(request.name()))
                .thenReturn(false);

        when(categoryRepository.findById(request.parentId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.createCategory(request)
        );

        verify(categoryRepository).existsByName(request.name());
        verify(categoryRepository).findById(request.parentId());
    }

    @Test
    void createCategory_ShouldReturnCreatedCategoryWithNullParent_WhenCategoryWithThisNameDoesNotExistAndParentIdIsNull() {

        var newCategoryName = "category";
        var request = new RequestCategoryDto(null, newCategoryName);
        when(categoryRepository.existsByName(request.name()))
                .thenReturn(false);

        var createdCategory = Category.builder()
                .id(1)
                .name(newCategoryName)
                .parentCategory(null)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .build();

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(createdCategory);

        var createdCategoryWithParentDto = new CategoryWithParentDto(1, newCategoryName, null);
        when(categoryMapper.toDtoWithParent(createdCategory))
                .thenReturn(createdCategoryWithParentDto);

        var result = categoryService.createCategory(request);

        assertEquals(createdCategoryWithParentDto, result);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toDtoWithParent(createdCategory);
    }

    @Test
    void createCategory_ShouldReturnCreatedCategoryWithParent_WhenCategoryWithThisNameDoesNotExistAndParentIsFound() {

        var newCategoryName = "category";
        var parentCategoryName = "parentCategory";
        var request = new RequestCategoryDto(1, "category");
        when(categoryRepository.existsByName(request.name()))
                .thenReturn(false);

        var parentCategory = Category.builder()
                .id(1)
                .name(parentCategoryName)
                .parentCategory(null)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .build();

        when(categoryRepository.findById(request.parentId()))
                .thenReturn(Optional.of(parentCategory));

        var createdCategory = Category.builder()
                .id(2)
                .name(newCategoryName)
                .parentCategory(parentCategory)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .build();

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(createdCategory);

        var parentCategoryWithParentDto = new CategoryWithParentDto(1, parentCategoryName, null);
        var createdCategoryWithParentDto = new CategoryWithParentDto(2, newCategoryName, parentCategoryWithParentDto);
        when(categoryMapper.toDtoWithParent(createdCategory))
                .thenReturn(createdCategoryWithParentDto);

        var result = categoryService.createCategory(request);

        assertEquals(createdCategoryWithParentDto, result);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toDtoWithParent(createdCategory);
    }

    @Test
    void updateCategory_ShouldThrowCategoryNotFoundExc_WhenCategoryNotFoundByIntegerIdentifier() {

        var request = new UpdateCategoryDto("updated");
        var identifier = "1";

        when(categoryRepository.findById(Integer.parseInt(identifier)))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategory(identifier, request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowCategoryNotFoundExc_WhenCategoryNotFoundByNameIdentifier() {

        var request = new UpdateCategoryDto("updated");
        var identifier = "category";

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategory(identifier, request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowCategoryAlreadyExistsExc_WhenCategoryWithUpdatedNameAlreadyExists() {

        var request = new UpdateCategoryDto("updated");
        var identifier = "category";
        var categoryToBeUpdated = Category.builder()
                .id(1)
                .name(identifier)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .parentCategory(null)
                .build();

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.of(categoryToBeUpdated));

        when(categoryRepository.existsByName(request.name()))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.updateCategory(identifier, request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateCategoryAndReturnUpdatedDto_WhenCategoryFoundByIdentifierAndNoCategoryExistWithNewName() {

        var newName = "updated";
        var request = new UpdateCategoryDto(newName);
        var identifier = "category";

        var categoryToBeUpdated = Category.builder()
                .id(1)
                .name(identifier)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .parentCategory(null)
                .build();

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.of(categoryToBeUpdated));

        when(categoryRepository.existsByName(request.name()))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(categoryToBeUpdated);

        var createdCategoryWithParentDto = new CategoryWithParentDto(1, newName, null);

        when(categoryMapper.toDtoWithParent(categoryToBeUpdated))
                .thenReturn(createdCategoryWithParentDto);

        var result = categoryService.updateCategory(identifier, request);

        assertEquals(createdCategoryWithParentDto, result);
        verify(categoryRepository).save(categoryToBeUpdated);
    }

    @Test
    void deleteCategory_ShouldThrowCategoryNotFoundExc_WhenCategoryNotFoundByIntegerIdentifier() {

        var identifier = "1";

        when(categoryRepository.findById(Integer.parseInt(identifier)))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(identifier)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldThrowCategoryNotFoundExc_WhenCategoryNotFoundByNameIdentifier() {

        var identifier = "category";

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(identifier)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategoryAndReturnDeletedDto_WhenCategoryFoundByIdentifier() {

        var identifier = "category";
        var categoryToBeDeleted = Category.builder()
                .id(1)
                .name(identifier)
                .childCategories(Collections.emptySet())
                .products(Collections.emptyList())
                .parentCategory(null)
                .build();

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.of(categoryToBeDeleted));

        var deletedCategoryWithParentDto = new CategoryWithChildrenDto(1, identifier, Collections.emptySet());

        when(categoryMapper.toDtoWithChildren(categoryToBeDeleted))
                .thenReturn(deletedCategoryWithParentDto);

        var result = categoryService.deleteCategory(identifier);

        assertEquals(deletedCategoryWithParentDto, result);
        verify(categoryRepository).delete(any(Category.class));
    }

    @Test
    void getProductsByCategory_ShouldThrowCategoryNotFoundExc_WhenCategoryNotFoundByIdentifier() {

        var identifier = "category";
        var pageable = PageRequest.of(0, 10);
        Predicate<Product> filter = (p) -> true;

        when(categoryRepository.findByName(identifier))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getProductsByCategory(identifier, pageable, filter)
        );
    }
}