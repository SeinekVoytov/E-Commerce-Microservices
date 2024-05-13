package org.example.orderservice.dto.order;

import org.example.orderservice.model.order.delivery.DeliveryStatus;

import java.util.List;
import java.util.UUID;

public record OrderDto(
        int id,
        UUID userId,
        DeliveryStatus status,
        List<OrderItemDto> items
) {
}
