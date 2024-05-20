package org.example.userservice.dto;

public record CartItemRequest (
        int productId,
        int quantity
) {
}
