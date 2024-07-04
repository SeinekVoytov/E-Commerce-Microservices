package org.example.userservice.exception;

public class ProductNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Product could not be found";

    public ProductNotFoundException() {
        super(MESSAGE);
    }
}
