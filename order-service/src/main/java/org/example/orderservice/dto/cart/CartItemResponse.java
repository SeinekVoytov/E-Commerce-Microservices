package org.example.orderservice.dto.cart;

import org.example.orderservice.dto.product.ProductDetailsDto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        ProductDetailsDto product,
        Integer quantity,
        BigDecimal totalPrice
) {
}
