package org.example.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateInventoryItemQuantityRequest(

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be a positive integer")
        Integer quantity,

        @NotNull(message = "Operation type cannot be null")
        OperationType operationType
) {

    public enum OperationType {

        INCREMENT,
        DECREMENT,
        SET;

        @Override
        @JsonValue
        public String toString() {
            return name().toLowerCase();
        }
    }
}
