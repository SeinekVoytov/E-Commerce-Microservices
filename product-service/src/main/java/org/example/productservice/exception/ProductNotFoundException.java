package org.example.productservice.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Product could not be found";

    public ProductNotFoundException() {
        super(MESSAGE);
    }
}
