package org.example.productservice.communication.dto;

public record InventoryItemRequest(
        Integer productId,
        Integer quantity
) {
}
