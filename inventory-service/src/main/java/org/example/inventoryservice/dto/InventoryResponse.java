package org.example.inventoryservice.dto;

import java.util.List;

public record InventoryResponse(
        List<InventoryItemResponse> inventoryItems
) {
}
