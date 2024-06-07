package org.example.orderservice.dto.order.delivery;

public record FeeDto (
        Float amount,
        String currency
) {
}
