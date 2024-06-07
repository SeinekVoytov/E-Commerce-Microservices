package org.example.orderservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLongDto {

    private String name;
    private List<String> images;
    private PriceDto price;
    private List<CategoryDto> categories;
    private Float lengthInM;
    private Float widthInM;
    private Float heightInM;
    private Float netWeightInKg;
    private Float grossWeightInKg;
}
