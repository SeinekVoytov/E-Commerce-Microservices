package org.example.userservice.exception;

public class CartIsEmptyException extends RuntimeException {

    public CartIsEmptyException() {
        super("Cart is empty : nothing to order");
    }
}
