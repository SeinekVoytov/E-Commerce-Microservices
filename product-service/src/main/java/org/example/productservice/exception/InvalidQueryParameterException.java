package org.example.productservice.exception;

public class InvalidQueryParameterException extends RuntimeException {
    public InvalidQueryParameterException(String message) {
        super(message);
    }
}
