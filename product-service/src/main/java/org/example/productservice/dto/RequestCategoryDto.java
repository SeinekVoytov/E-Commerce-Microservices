package org.example.productservice.dto;

public record RequestCategoryDto(
        Integer parentId,
        String name
) {
}