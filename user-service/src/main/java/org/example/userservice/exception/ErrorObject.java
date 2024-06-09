package org.example.userservice.exception;

import java.util.Date;

public record ErrorObject (
        Integer statusCode,
        String message,
        Date timestamp
) {
}