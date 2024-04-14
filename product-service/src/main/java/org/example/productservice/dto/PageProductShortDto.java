package org.example.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageProductShortDto {
    private List<ProductShortDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
