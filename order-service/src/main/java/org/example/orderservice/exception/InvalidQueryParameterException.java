package org.example.orderservice.exception;

public class InvalidQueryParameterException extends RuntimeException {

    public InvalidQueryParameterException(String paramName, String paramValue) {
        super(String.format("Invalid value '%s' for parameter '%s'", paramValue, paramName));
    }
}
