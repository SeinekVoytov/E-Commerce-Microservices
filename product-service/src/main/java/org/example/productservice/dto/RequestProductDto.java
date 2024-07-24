package org.example.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

@Builder
public record RequestProductDto(
     @NotBlank(message = "Name cannot be blank")
     String name,

     @NotBlank(message = "Description cannot be blank")
     String description,

     @NotBlank(message = "Brand cannot be blank")
     String brand,

     @NotNull(message = "Images set cannot be null")
     Set<@URL(message = "Image URL must be a valid") String> images,

     @NotNull(message = "Price amount cannot be null")
     @DecimalMin(value = "0", inclusive = false, message = "Price amount must be greater than zero")
     BigDecimal priceAmount,

     @NotNull(message = "Price currency cannot be null")
     Currency priceCurrency,

     @NotBlank(message = "Country manufacturer cannot be blank")
     @Size(min = 2, max = 64, message = "Country length should be between 2 and 64")
     String countryManufacturer,

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
     Double grossWeightInKg,

     @NotNull(message = "Inventory quantity cannot be null")
     @Positive(message = "Inventory quantity must be a positive integer")
     Integer inventoryQuantity
) {

}