package org.example.orderservice.dto.order;

import org.example.orderservice.model.order.delivery.DeliveryStatus;

import java.util.Set;
import java.util.UUID;

public record OrderDto (
        Integer id,
        UUID userId,
        DeliveryStatus status,
        Set<OrderItemDto> items
) {
}