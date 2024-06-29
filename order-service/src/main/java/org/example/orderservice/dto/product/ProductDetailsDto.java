package org.example.orderservice.dto.product;

import lombok.Builder;

import java.util.Set;

@Builder
public record ProductDetailsDto (
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