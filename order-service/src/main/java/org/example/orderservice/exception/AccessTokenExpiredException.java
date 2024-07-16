package org.example.orderservice.exception;

public class AccessTokenExpiredException extends RuntimeException {

    private static final String MESSAGE = "Access Token has expired";

    public AccessTokenExpiredException() {
        super(MESSAGE);
    }
}
