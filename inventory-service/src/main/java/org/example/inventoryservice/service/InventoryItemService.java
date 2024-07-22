package org.example.inventoryservice.service;

import org.example.inventoryservice.dto.InventoryItemRequest;
import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.dto.InventoryResponse;
import org.example.inventoryservice.dto.UpdateInventoryItemQuantityRequest;

public interface InventoryItemService {

    InventoryItemResponse getInventoryItemByProductId(Integer productId);

    InventoryResponse getAllInventory();

    InventoryItemResponse addInventoryItem(InventoryItemRequest request);

    InventoryItemResponse updateInventoryItemQuantity(UpdateInventoryItemQuantityRequest request);

    InventoryItemResponse deleteInventoryItem(Integer productId);
}
