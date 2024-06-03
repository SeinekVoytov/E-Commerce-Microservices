package org.example.productservice.dto;

import java.util.List;

public record ProductShortDto (
     int id,
     String name,
     List<String> images,
     PriceDto price,
     List<CategoryDto> categories
) {
    
}
