package org.example.productservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductLongDto (
     int id,
     String name,
     List<String> images,
     PriceDto price,
     List<CategoryDto> categories,
     Float lengthInM,
     Float widthInM,
     Float heightInM,
     Float netWeightInKg,
     Float grossWeightInKg
) {

}
