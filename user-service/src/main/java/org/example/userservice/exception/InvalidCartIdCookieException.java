package org.example.userservice.exception;

import java.util.UUID;

public class InvalidCartIdCookieException extends RuntimeException {

    public InvalidCartIdCookieException(UUID value) {
        super(String.format("Invalid cart id cookie value: %s", value));
    }
}
