package org.example.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.productservice.dto.CategoryDto;
import org.example.productservice.dto.CategoryWithChildrenDto;
import org.example.productservice.dto.CategoryWithParentDto;
import org.example.productservice.dto.RequestCategoryDto;
import org.example.productservice.dto.UpdateCategoryDto;
import org.example.productservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Set<?>> getAllCategories(@RequestParam(required = false) boolean withParents,
                                                   @RequestParam(required = false) boolean withChildren) {
        return ResponseEntity.ok(categoryService.getAllCategories(withParents, withChildren));
    }

    @GetMapping("search")
    public ResponseEntity<List<CategoryDto>> search(@RequestParam String keyword) {
        List<CategoryDto> foundCategories = categoryService.search(keyword);
        return ResponseEntity.ok(foundCategories);
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<CategoryDto>> reindex() {
        List<CategoryDto> reindexed = categoryService.reindex();
        return ResponseEntity.ok(reindexed);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<?> getCategoryByIdentifier(@PathVariable String identifier,
                                                     @RequestParam(required = false) boolean withParents,
                                                     @RequestParam(required = false) boolean withChildren) {

        return ResponseEntity.ok(
                categoryService.getCategoryByIdentifier(identifier, withParents, withChildren)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryWithParentDto> createCategory(@RequestBody @Valid RequestCategoryDto data) {
        CategoryWithParentDto response = categoryService.createCategory(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{identifier}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryWithParentDto> updateCategory(@PathVariable String identifier,
                                                                @RequestBody @Valid UpdateCategoryDto data) {
        return ResponseEntity.ok(categoryService.updateCategory(identifier, data));
    }

    @DeleteMapping("/{identifier}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryWithChildrenDto> deleteCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(categoryService.deleteCategory(identifier));
    }
}