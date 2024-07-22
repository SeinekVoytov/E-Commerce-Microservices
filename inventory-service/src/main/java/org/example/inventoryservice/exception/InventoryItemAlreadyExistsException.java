package org.example.inventoryservice.exception;

public class InventoryItemAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Inventory item for product with id=%s already exists";

    public InventoryItemAlreadyExistsException(Integer id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
