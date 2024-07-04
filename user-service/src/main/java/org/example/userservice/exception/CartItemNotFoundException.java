package org.example.userservice.exception;

public class CartItemNotFoundException extends RuntimeException {

    public static final String MESSAGE = "No such item found in user's cart";

    public CartItemNotFoundException() {
        super(MESSAGE);
    }
}
