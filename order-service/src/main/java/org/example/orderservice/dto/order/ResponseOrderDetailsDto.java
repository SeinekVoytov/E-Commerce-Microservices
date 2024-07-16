package org.example.orderservice.dto.order;

import org.example.orderservice.dto.order.delivery.DeliveryDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record OrderDetailsDto (
        Integer id,
        UUID userId,
        DeliveryDto delivery,
        AddressDto address,
        Set<OrderItemDto> items,
        Instant createdAt
) {
}