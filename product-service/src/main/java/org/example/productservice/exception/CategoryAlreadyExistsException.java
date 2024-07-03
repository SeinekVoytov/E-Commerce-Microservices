package org.example.productservice.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Category with name %s already exists";

    public CategoryAlreadyExistsException(String name) {
        super(String.format(MESSAGE_FORMAT, name));
    }
}
