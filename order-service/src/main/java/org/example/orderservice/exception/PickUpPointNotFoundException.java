package org.example.orderservice.exception;

import java.util.UUID;

public class PickUpPointNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Pick-up point is id = %s not found";

    public PickUpPointNotFoundException(UUID id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
