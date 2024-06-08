package org.example.orderservice.dto.order;

public record AddressDto (
        String city,
        String country,
        String streetAddress,
        String apartment
) {
}