package org.example.userservice.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException() {
        super("No cart associated with this user");
    }
}

