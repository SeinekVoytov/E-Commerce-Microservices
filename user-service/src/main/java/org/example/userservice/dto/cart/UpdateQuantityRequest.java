package org.example.userservice.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateQuantityRequest(
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be a positive integer")
        Integer quantity
) {
}