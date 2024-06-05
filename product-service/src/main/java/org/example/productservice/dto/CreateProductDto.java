package org.example.productservice.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

public record CreateProductDto (
     String name,
     BigDecimal priceAmount,
     Currency priceCurrency,
     Set<Integer> categoryIds,
     Double lengthInM,
     Double widthInM,
     Double heightInM,
     Double netWeightInKg,
     Double grossWeightInKg
) {

}