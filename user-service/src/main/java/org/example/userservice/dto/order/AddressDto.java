package org.example.userservice.dto.order;

public record AddressDto(
        String city,
        String country,
        String streetAddress,
        String apartment
) {
}