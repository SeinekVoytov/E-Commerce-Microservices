package org.example.userservice.dto.cart;

import org.example.userservice.dto.product.ProductDetailsDto;

import java.math.BigDecimal;

public record CartItemResponse (
        Integer id,
        ProductDetailsDto product,
        Integer quantity,
        BigDecimal totalPrice
) {
}