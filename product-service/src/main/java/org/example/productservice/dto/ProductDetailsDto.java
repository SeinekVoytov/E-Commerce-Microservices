package org.example.productservice.dto;

import lombok.Builder;

import java.util.Set;

@Builder
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
