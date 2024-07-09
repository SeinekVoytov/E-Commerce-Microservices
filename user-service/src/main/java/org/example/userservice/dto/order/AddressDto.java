package org.example.userservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 2, max = 64, message = "City length must be between 2 and 64")
        String city,

        @NotBlank(message = "Country cannot be blank")
        @Size(min = 2, max = 64, message = "City length must be between 2 and 64")
        String country,

        @NotBlank(message = "Country cannot be blank")
        String streetAddress,

        @NotBlank(message = "Apartment cannot be blank")
        String apartment
) {
}