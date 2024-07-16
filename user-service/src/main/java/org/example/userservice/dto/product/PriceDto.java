package org.example.userservice.dto.product;

import java.math.BigDecimal;

public record PriceDto (
        BigDecimal amount,
        String currency
) {
}