package org.example.orderservice.exception;

public class CartNotFoundException extends RuntimeException {

    public static final String MESSAGE = "No cart associated with this user";

    public CartNotFoundException() {
        super(MESSAGE);
    }
}