package org.example.productservice.dto;

import lombok.Builder;

@Builder
public record PriceDto (
        Float amount,
        String currency
) {

}
