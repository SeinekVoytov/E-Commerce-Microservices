package org.example.userservice.dto;

public record CartItemResponse (
    ProductLongDto product,
    int quantity
) {
}
