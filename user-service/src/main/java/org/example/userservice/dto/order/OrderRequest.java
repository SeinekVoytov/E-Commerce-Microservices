package org.example.userservice.dto.order;

public record OrderRequest(
        DeliveryType deliveryType,
        AddressDto address
) {
}