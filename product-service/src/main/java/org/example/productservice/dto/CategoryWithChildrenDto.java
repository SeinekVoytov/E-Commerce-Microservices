package org.example.productservice.dto;

import java.util.Set;

public record CategoryWithChildrenDto(
        Integer id,
        String name,
        Set<CategoryWithChildrenDto> children
) {
}