package org.example.userservice.dto.cart;

import org.example.userservice.dto.product.ProductDetailsDto;

public record CartItemResponse (
        Integer id,
        ProductDetailsDto product,
        Integer quantity
) {
}