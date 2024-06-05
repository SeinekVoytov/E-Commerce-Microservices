package org.example.productservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PriceDto (
        BigDecimal amount,
        String currency
) {

}
