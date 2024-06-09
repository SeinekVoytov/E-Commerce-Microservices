package org.example.userservice.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException() {
        super("No such item found in user's cart");
    }
}
