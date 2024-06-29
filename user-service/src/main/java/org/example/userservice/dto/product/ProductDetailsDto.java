package org.example.userservice.dto.product;

import java.util.Set;

public record ProductDetailsDto(
     Integer id,
     String name,
     String description,
     Set<String> images,
     PriceDto price,
     Set<CategoryDto> categories,
     Double lengthInMeters,
     Double widthInMeters,
     Double heightInMeters,
     Double netWeightInKg,
     Double grossWeightInKg
) {
}