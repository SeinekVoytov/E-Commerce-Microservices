package org.example.productservice.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer id) {
        super(String.format("Category with id '%d' not found", id));
    }

    public CategoryNotFoundException(String name) {
        super(String.format("Category with name '%s' not found", name));
    }
}
