package org.example.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException() {
        super("Order could not be found");
    }
}
