package org.example.orderservice.dto.order;

import org.example.orderservice.model.DeliveryStatus;

import java.util.UUID;

public record DeliveryDto (
    UUID pickUpPointId,
    String pickUpPointAddress,
    DeliveryStatus deliveryStatus
) {
}
