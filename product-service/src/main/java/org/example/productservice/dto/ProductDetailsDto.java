package org.example.productservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ProductDetailsDto(
        Integer id,
        String name,
        String description,
        String brand,
        Set<String> images,
        PriceDto price,
        List<CategoryDto> categories,
        String countryManufacturer,
        Double lengthInMeters,
        Double widthInMeters,
        Double heightInMeters,
        Double netWeightInKg,
        Double grossWeightInKg
) {

}