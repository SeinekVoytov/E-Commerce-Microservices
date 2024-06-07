package org.example.orderservice.dto.order.delivery;

import org.example.orderservice.model.order.delivery.DeliveryStatus;
import org.example.orderservice.model.order.delivery.DeliveryType;

public record DeliveryDto (
        DeliveryType type,
        DeliveryStatus status,
        FeeDto fee
) {
}
