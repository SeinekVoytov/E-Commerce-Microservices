package org.example.userservice.dto.cart;

import org.example.userservice.dto.product.ProductDetailsDto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse (
        UUID id,
        ProductDetailsDto product,
        Integer quantity,
        BigDecimal totalPrice
) {
}