package org.example.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

@Builder
public record RequestProductDto(
     @NotBlank(message = "Name cannot be blank")
     String name,

     @NotBlank(message = "Description cannot be blank")
     String description,

     @NotNull(message = "Images set cannot be null")
     Set<@NotBlank(message = "Image URL cannot be blank") String> images,

     @NotNull(message = "Price amount cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Price amount must be greater than zero")
     BigDecimal priceAmount,

     @NotNull(message = "Price currency cannot be null")
     Currency priceCurrency,

     @NotNull(message = "Category IDs set cannot be null")
     Set<@Positive(message = "Category ID must be a positive integer") Integer> categoryIds,

     @NotNull(message = "Length cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Length must be greater than zero")
     Double lengthInMeters,

     @NotNull(message = "Width cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Width must be greater than zero")
     Double widthInMeters,

     @NotNull(message = "Height cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Height must be greater than zero")
     Double heightInMeters,

     @NotNull(message = "Net weight cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Net weight must be greater than zero")
     Double netWeightInKg,

     @NotNull(message = "Gross weight cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Gross weight must be greater than zero")
     Double grossWeightInKg
) {

}