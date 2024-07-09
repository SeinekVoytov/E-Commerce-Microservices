package org.example.orderservice.dto.product;

import java.util.Set;

public record CategoryWithChildrenDto(
        Integer id,
        String name,
        Set<CategoryWithChildrenDto> children
) {
}