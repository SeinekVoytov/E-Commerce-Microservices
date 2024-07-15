package org.example.userservice.exception;

public class ProductNotFoundException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Product with id=%d could not be found";

    public ProductNotFoundException(Integer id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
