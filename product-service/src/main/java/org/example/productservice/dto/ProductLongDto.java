package org.example.productservice.dto;

import lombok.Builder;
import lombok.Data;
import org.example.productservice.model.Category;
import org.example.productservice.model.Price;

import java.util.List;

@Data
@Builder
public class ProductLongDto {
    private int id;
    private String name;
    private List<String> images;
    private Price price;
    private List<Category> categories;
    private float lengthInM;
    private float widthInM;
    private float heightInM;
    private float netWeightInKg;
    private float grossWeightInKg;
}
