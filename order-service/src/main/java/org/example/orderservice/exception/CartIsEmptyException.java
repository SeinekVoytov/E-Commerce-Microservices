package org.example.orderservice.exception;

public class CartIsEmptyException extends RuntimeException {

    private static final String MESSAGE = "Cart is empty : nothing to order";

    public CartIsEmptyException() {
        super(MESSAGE);
    }
}
