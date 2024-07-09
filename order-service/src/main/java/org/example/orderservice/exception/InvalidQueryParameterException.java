package org.example.orderservice.exception;

public class InvalidQueryParameterException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Invalid value '%s' for parameter '%s'";

    public InvalidQueryParameterException(String paramName, String paramValue) {
        super(String.format(MESSAGE_FORMAT, paramValue, paramName));
    }
}
