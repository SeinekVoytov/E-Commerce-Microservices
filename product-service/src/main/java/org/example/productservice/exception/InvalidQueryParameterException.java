package org.example.productservice.exception;

public class InvalidQueryParameterException extends RuntimeException {

    private static final String MESSAGE = "Invalid query parameter value";

    public InvalidQueryParameterException() {
        super(MESSAGE);
    }
}
