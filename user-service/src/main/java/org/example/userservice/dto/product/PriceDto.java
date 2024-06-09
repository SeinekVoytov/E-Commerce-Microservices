package org.example.userservice.dto.product;

import java.math.BigDecimal;
import java.util.Currency;

public record PriceDto (
        BigDecimal amount,
        Currency currency
) {
}