package org.example.userservice.dto.cart;

public record CartItemRequest (
        Integer productId,
        Integer quantity
) {
}