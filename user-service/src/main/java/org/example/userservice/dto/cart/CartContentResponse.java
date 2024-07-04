package org.example.userservice.dto.cart;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

public record CartContentResponse(
        Set<CartItemResponse> items,
        Map<Currency, BigDecimal> totalPrices,
        Integer totalItems
) {
}