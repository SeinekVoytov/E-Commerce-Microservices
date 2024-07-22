package org.example.inventoryservice.exception;

public class NotSufficientAmountOfProductException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Not sufficient amount: requested to decrement: %d, available: %d";

    public NotSufficientAmountOfProductException(int requestedAmount, int availableAmount) {
        super(String.format(MESSAGE_FORMAT, requestedAmount, availableAmount));
    }
}
