package org.example.productservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ProductDto(
     Integer id,
     String name,
     Double netWeightInKg,
     String description,
     String brand,
     Set<String> images,
     PriceDto price,
     List<CategoryDto> categories
) {
    
}