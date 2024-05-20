package org.example.userservice.dto;

import org.example.userservice.model.product.ProductLong;

import java.util.UUID;

public record AnonymousCartItemResponse (
        UUID cartId,
        ProductLong product,
        int quantity
) {
}
