package org.example.userservice.exception;

import java.util.UUID;

public class InvalidCartIdCookieException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "Invalid cart id cookie value: %s";

    public InvalidCartIdCookieException(UUID value) {
        super(String.format(MESSAGE_FORMAT, value));
    }
}
