package org.example.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Order could not be found";

    public OrderNotFoundException() {
        super(MESSAGE);
    }
}
