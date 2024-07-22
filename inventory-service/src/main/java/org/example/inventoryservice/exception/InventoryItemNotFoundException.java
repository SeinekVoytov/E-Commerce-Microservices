package org.example.inventoryservice.exception;

public class InventoryItemNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Inventory item for product with id=%s not found";

    public InventoryItemNotFoundException(Integer productId) {
        super(String.format(MESSAGE_FORMAT, productId));
    }
}
