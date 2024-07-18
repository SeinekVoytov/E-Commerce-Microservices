package org.example.orderservice.dto.product;

import java.util.List;

public record BulkProductsResponse(
        List<ProductDetailsDto> products
) {
}
