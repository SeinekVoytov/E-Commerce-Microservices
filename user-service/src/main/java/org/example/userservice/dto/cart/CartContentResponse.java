package org.example.userservice.dto.cart;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public record CartContentResponse(
        List<CartItemResponse> items,
        Map<Currency, BigDecimal> totalPrices
) {
}