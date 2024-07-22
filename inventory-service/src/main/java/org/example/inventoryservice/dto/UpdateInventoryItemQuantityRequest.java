package org.example.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public record UpdateInventoryItemQuantityRequest(
        Integer quantity,
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
