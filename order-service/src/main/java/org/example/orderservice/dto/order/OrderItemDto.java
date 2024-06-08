package org.example.orderservice.dto.order;

import org.example.orderservice.dto.product.ProductDetailsDto;

public record OrderItemDto (
        ProductDetailsDto product,
        Integer quantity
) {
}