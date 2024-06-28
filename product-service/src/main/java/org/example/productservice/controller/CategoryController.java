package org.example.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Set<CategoryWithChildrenDto>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryWithParentDto> createCategory(@RequestBody RequestCategoryDto data) {
        CategoryWithParentDto response = categoryService.createCategory(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{identifier}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryWithChildrenDto> deleteCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(categoryService.deleteCategory(identifier));
    }
}