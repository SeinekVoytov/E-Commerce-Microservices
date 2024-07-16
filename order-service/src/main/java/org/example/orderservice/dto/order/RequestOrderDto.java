package org.example.orderservice.dto.order;

import org.example.orderservice.dto.order.delivery.DeliveryDto;

public record RequestOrderDto(
    DeliveryDto delivery,
    AddressDto address
) {
}
