package org.example.userservice.dto;

public record CartItemRequest (
        int product_id,
        int quantity
) {
}
