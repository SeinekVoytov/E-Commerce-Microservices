package org.example.inventoryservice.dto;

public record InventoryItemRequest(
        Integer productId,
        Integer quantity
) {
}
