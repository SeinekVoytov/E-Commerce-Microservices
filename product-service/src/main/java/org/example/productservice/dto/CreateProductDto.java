package org.example.productservice.dto;

import java.util.List;

public record CreateProductDto (
     String name,
     Float priceAmount,
     String priceCurrency,
     List<Integer> categoryIds,
     Float lengthInM,
     Float widthInM,
     Float heightInM,
     Float netWeightInKg,
     Float grossWeightInKg
) {

}