package org.example.orderservice.dto.order.delivery;

import java.math.BigDecimal;
import java.util.Currency;

public record FeeDto (
        BigDecimal amount,
        Currency currency
) {
}
