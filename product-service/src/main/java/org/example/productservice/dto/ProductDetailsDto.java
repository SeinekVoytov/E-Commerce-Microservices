package org.example.productservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ProductDetailsDto(
     Integer id,
     String name,
     String description,
     Set<String> images,
     PriceDto price,
     List<CategoryDto> categories,
     Double lengthInMeters,
     Double widthInMeters,
     Double heightInMeters,
     Double netWeightInKg,
     Double grossWeightInKg
) {

}