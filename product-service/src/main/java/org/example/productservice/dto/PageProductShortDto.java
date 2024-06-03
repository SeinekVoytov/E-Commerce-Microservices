package org.example.productservice.dto;

import java.util.List;

public record PageProductShortDto (
     List<ProductShortDto> content,
     int pageNo,
     int pageSize,
     long totalElements,
     int totalPages
) {

}
