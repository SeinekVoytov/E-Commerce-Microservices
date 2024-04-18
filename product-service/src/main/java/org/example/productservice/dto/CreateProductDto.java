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
public class CreateProductDto {

    private String name;
    private float priceAmount;
    private String priceCurrency;
    private List<Integer> categoryIds;
    private Float lengthInM;
    private Float widthInM;
    private Float heightInM;
    private Float netWeightInKg;
    private Float grossWeightInKg;
}
