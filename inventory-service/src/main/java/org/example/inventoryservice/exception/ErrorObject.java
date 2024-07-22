package org.example.inventoryservice.exception;

import java.util.Date;

public record ErrorObject(
        Integer statusCode,
        String message,
        Date timestamp
) {
}
