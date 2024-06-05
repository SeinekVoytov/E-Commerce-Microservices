package org.example.productservice.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

public record CreateProductDto (
     String name,
     Set<String> images,
     BigDecimal priceAmount,
     Currency priceCurrency,
     Set<Integer> categoryIds,
     Double lengthInMeters,
     Double widthInMeters,
     Double heightInMeters,
     Double netWeightInKg,
     Double grossWeightInKg
) {

}