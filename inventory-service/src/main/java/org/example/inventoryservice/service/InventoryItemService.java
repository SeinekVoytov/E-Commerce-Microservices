package org.example.inventoryservice.service;

import org.example.inventoryservice.dto.InventoryItemRequest;
import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.dto.InventoryResponse;
import org.example.inventoryservice.dto.UpdateInventoryItemQuantityRequest;
import org.springframework.transaction.annotation.Transactional;

public interface InventoryItemService {

    InventoryItemResponse getInventoryItemByProductId(Integer productId);

    InventoryResponse getAllInventory();

    InventoryItemResponse addInventoryItem(InventoryItemRequest request);

    @Transactional
    InventoryItemResponse updateInventoryItemQuantity(Integer productId,
                                                      UpdateInventoryItemQuantityRequest request);

    InventoryItemResponse deleteInventoryItem(Integer productId);
}
