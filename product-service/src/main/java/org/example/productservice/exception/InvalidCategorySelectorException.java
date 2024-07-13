package org.example.productservice.exception;

public class InvalidCategorySelectorException extends RuntimeException {

    private static final String MESSAGE = "parameters 'withParents' & 'withChildren' cannot both be true";

    public InvalidCategorySelectorException() {
        super(MESSAGE);
    }
}
