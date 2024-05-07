package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductShortDto {

    private String name;
    private List<String> images;
    private PriceDto price;
    private List<CategoryDto> categories;
}
