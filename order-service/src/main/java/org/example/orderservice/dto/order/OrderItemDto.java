package org.example.orderservice.dto.order;

import org.example.orderservice.dto.product.ProductLongDto;

public record OrderItemDto (
        ProductLongDto product,
        int quantity
) {
}
