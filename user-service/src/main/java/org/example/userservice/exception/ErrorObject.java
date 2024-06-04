package org.example.userservice.exception;

import java.util.Date;

public record ErrorObject (
        int statusCode,
        String message,
        Date timestamp
) {
}
