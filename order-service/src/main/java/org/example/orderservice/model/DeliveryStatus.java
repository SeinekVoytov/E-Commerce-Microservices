package org.example.orderservice.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryStatus {
    ORDER_RECEIVED, ORDER_PROCESSING, IN_TRANSIT, DELIVERED, CANCELLED;

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}
