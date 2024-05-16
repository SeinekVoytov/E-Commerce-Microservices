package org.example.userservice.dto;

import org.example.userservice.model.product.Category;
import org.example.userservice.model.product.Price;

import java.util.List;

public record ProductLongDto (
     int id,
     String name,
     List<String> images,
     Price price,
     List<Category> categories,
     Float lengthInM,
     Float widthInM,
     Float heightInM,
     Float netWeightInKg,
     Float grossWeightInKg
) {
}
