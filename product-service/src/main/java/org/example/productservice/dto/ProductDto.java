package org.example.productservice.dto;

import java.util.Set;

public record ProductDto(
     Integer id,
     String name,
     Set<String> images,
     PriceDto price,
     Set<CategoryDto> categories
) {
    
}
