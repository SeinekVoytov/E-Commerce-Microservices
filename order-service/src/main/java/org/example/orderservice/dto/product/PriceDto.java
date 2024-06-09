package org.example.orderservice.dto.product;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Currency;

@Builder
public record PriceDto (
        BigDecimal amount,
        Currency currency
) {
}