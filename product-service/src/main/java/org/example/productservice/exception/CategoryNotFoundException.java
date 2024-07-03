package org.example.productservice.exception;

public class CategoryNotFoundException extends RuntimeException {

    public static final String MESSAGE_FORMAT_ID = "Category with id '%d' not found";
    public static final String MESSAGE_FORMAT_NAME = "Category with name '%s' not found";

    public CategoryNotFoundException(Integer id) {
        super(String.format(MESSAGE_FORMAT_ID, id));
    }

    public CategoryNotFoundException(String name) {
        super(String.format(MESSAGE_FORMAT_NAME, name));
    }
}
