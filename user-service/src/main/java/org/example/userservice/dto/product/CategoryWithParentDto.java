package org.example.productservice.dto;

public record CategoryWithParentDto(
        Integer id,
        String name,
        CategoryWithParentDto parent
) {
}