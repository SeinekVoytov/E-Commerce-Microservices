package org.example.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RequestCategoryDto(
        @Positive(message = "Parent id must be either null or positive integer")
        Integer parentId,

        @NotBlank(message = "Category name cannot be blank")
        String name
) {
}