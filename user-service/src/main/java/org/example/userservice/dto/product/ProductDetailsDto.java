package org.example.userservice.dto.product;

import java.util.List;
import java.util.Set;

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