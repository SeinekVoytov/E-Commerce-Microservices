package org.example.userservice.dto.order;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryType {

    STANDARD, EXPRESS, SAME_DAY, NEXT_DAY, SCHEDULED, IN_STORE_PICKUP;

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}