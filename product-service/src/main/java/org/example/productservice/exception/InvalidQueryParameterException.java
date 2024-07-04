package org.example.productservice.exception;

public class InvalidQueryParameterException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Invalid value '%s' for parameter '%s'";

    public InvalidQueryParameterException(String paramName, String paramValue) {
        super(String.format(MESSAGE_FORMAT, paramValue, paramName));
    }
}
