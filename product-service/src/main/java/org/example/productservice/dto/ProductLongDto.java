package org.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.productservice.model.Category;
import org.example.productservice.model.Price;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLongDto {
    private int id;
    private String name;
    private List<String> images;
    private Price price;
    private List<Category> categories;
    private Float lengthInM;
    private Float widthInM;
    private Float heightInM;
    private Float netWeightInKg;
    private Float grossWeightInKg;
}
