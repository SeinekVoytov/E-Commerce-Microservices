package org.example.userservice.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("Product could not be found");
    }
}
