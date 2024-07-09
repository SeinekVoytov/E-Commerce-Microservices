package org.example.userservice.dto.product;

public record CategoryWithParentDto(
        Integer id,
        String name,
        CategoryWithParentDto parent
) {
}