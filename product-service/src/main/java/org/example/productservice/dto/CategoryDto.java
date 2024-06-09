package org.example.productservice.dto;

import lombok.Builder;

@Builder
public record CategoryDto (
        String name,
        Integer count
) {

}
