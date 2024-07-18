package org.example.orderservice.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequest(
        @NotNull(message = "Pick-up point id cannot be null")
        UUID pickUpPointId
) {
}
