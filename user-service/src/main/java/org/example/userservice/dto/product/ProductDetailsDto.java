package org.example.userservice.dto.product;

import java.util.Set;

public record ProductDetailsDto(
     Integer id,
     String name,
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