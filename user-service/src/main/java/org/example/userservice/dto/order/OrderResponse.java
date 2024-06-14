package org.example.userservice.dto.order;

import org.example.userservice.dto.cart.CartContentResponse;

public record OrderResponse(
    DeliveryType deliveryType,
    AddressDto address,
    CartContentResponse cartContent
) {
}