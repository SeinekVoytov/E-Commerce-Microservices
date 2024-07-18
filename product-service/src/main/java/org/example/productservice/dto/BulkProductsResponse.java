package org.example.productservice.dto;

import java.util.List;

public record BulkProductsResponse(
        List<ProductDetailsDto> products
) {
}
