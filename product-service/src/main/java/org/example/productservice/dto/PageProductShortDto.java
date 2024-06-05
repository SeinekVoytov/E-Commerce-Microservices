package org.example.productservice.dto;

import java.util.Set;

public record PageProductShortDto (
     Set<ProductDto> content,
     int pageNo,
     int pageSize,
     long totalElements,
     int totalPages
) {

}
