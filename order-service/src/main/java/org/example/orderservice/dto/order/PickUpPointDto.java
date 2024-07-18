package org.example.orderservice.dto.order;

import java.util.UUID;

public record PickUpPointDto(
        UUID id,
        String address
) {
}
