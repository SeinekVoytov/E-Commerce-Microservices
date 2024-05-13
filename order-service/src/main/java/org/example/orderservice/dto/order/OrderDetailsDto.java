package org.example.orderservice.dto.order;

import org.example.orderservice.dto.order.delivery.DeliveryDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record OrderDetailsDto (
        int id,
        UUID userId,
        DeliveryDto delivery,
        AddressDto address,
        List<OrderItemDto> items,
        Date createdAt
) {
}