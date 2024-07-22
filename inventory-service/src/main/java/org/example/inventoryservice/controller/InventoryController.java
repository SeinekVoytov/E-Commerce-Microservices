package org.example.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.dto.InventoryItemRequest;
import org.example.inventoryservice.dto.InventoryItemResponse;
import org.example.inventoryservice.dto.InventoryResponse;
import org.example.inventoryservice.dto.UpdateInventoryItemQuantityRequest;
import org.example.inventoryservice.service.InventoryItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("items")
    public ResponseEntity<InventoryResponse> getInventory() {
        return ResponseEntity.ok(inventoryItemService.getAllInventory());
    }

    @GetMapping("/items/{productId}")
    public ResponseEntity<InventoryItemResponse> getInventoryByProductId(
            @PathVariable Integer productId
    ) {
        return ResponseEntity.ok(inventoryItemService.getInventoryItemByProductId(productId));
    }

    @PostMapping("/items")
    public ResponseEntity<InventoryItemResponse> createInventoryItem(
            @RequestBody InventoryItemRequest request
    ) {
        InventoryItemResponse result = inventoryItemService.addInventoryItem(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<InventoryItemResponse> updateInventoryItemQuantity(
            @PathVariable Integer productId,
            @RequestBody UpdateInventoryItemQuantityRequest request
    ) {
        InventoryItemResponse result =
                inventoryItemService.updateInventoryItemQuantity(productId, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<InventoryItemResponse> deleteInventoryItem(
            @PathVariable Integer productId
    ) {
        InventoryItemResponse result =
                inventoryItemService.deleteInventoryItem(productId);

        return ResponseEntity.ok(result);
    }
}
