package org.example.userservice.kafka.message;

import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.order.AddressDto;
import org.example.userservice.dto.order.DeliveryType;

import java.util.UUID;

public record OrderPublishedMessage(
        UUID userId,
        DeliveryType deliveryType,
        AddressDto address,
        CartContentResponse cartContent
) {
}