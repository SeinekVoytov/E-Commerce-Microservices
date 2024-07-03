package org.example.userservice.exception;

public class CartIsEmptyException extends RuntimeException {

    public static final String MESSAGE = "Cart is empty : nothing to order";

    public CartIsEmptyException() {
        super(MESSAGE);
    }
}
