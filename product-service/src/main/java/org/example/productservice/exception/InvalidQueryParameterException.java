package org.example.productservice.exception;

public class InvalidQueryParameterException extends RuntimeException {

    private static final String MESSAGE = "Invalid value '%s' for parameter '%s'";

    public InvalidQueryParameterException(String paramName, String paramValue) {
        super(String.format(MESSAGE, paramValue, paramName));
    }
}
