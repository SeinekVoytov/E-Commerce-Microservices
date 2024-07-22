package org.example.inventoryservice.exception;

public class InventoryItemAlreadyExists extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Inventory item for product with id=%s already exists";

    public InventoryItemAlreadyExists(Integer id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
