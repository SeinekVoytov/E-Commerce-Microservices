package org.example.productservice.dto;

import lombok.Builder;
import lombok.Data;
import org.example.productservice.model.Category;
import org.example.productservice.model.Price;

import java.util.List;

@Data
@Builder
public class ProductShortDto {
    private int id;
    private String name;
    private List<String> imgUrls;
    private Price price;
    private List<Category> categories;
}
