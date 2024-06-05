package org.example.productservice.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer id) {
        super(String.format("Category with id %d not found", id));
    }
}
