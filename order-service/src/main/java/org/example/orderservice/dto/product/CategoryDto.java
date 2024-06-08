package org.example.orderservice.dto.product;

import lombok.Builder;

@Builder
public record CategoryDto (
        String name,
        Integer count
) {

}