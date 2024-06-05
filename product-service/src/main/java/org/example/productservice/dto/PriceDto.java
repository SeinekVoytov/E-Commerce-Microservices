package org.example.productservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Currency;

@Builder
public record PriceDto (
        BigDecimal amount,
        Currency currency
) {

}
