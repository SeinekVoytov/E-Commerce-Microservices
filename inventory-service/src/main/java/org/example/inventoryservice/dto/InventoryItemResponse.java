package org.example.inventoryservice.dto;

public record InventoryItemResponse(
        Integer productId,
        Integer quantity
) {
}
