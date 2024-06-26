package org.example.productservice.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record ProductDto(
     Integer id,
     String name,
     Double netWeightInKg,
     String description,
     Set<String> images,
     PriceDto price,
     Set<CategoryDto> categories
) {
    
}