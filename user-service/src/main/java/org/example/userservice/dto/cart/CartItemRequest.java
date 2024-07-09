package org.example.userservice.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest (
        @NotNull(message = "Product id cannot be null")
        @Positive(message = "Product id must be a positive integer")
        Integer productId,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be a positive integer")
        Integer quantity
) {
}