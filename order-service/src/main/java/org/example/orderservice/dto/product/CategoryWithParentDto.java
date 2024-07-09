package org.example.orderservice.dto.product;

public record CategoryWithParentDto(
        Integer id,
        String name,
        CategoryWithParentDto parent
) {
}