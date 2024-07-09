package org.example.userservice.dto.product;

import java.util.Set;

public record CategoryWithChildrenDto(
        Integer id,
        String name,
        Set<CategoryWithChildrenDto> children
) {
}