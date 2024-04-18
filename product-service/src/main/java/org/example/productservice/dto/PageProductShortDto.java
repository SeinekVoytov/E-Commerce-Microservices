package org.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageProductShortDto {
    private List<ProductShortDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
