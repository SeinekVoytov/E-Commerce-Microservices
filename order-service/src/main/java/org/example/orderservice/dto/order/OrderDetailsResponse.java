package org.example.orderservice.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record OrderDetailsResponse(
        Integer id,
        UUID userId,
        DeliveryDto delivery,
        Set<OrderItemDto> items,
        Integer totalItems,
        Map<String, BigDecimal> totalPrices,
        Instant createdAt
) {
}