package org.example.userservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {

    private Integer id;
    private String name;
    private String description;
    private String brand;
    private String countryManufacturer;
    private Set<String> images = new HashSet<>();
    private PriceDto price;
    private List<CategoryDto> categories = new ArrayList<>();
    private Double lengthInMeters;
    private Double widthInMeters;
    private Double heightInMeters;
    private Double netWeightInKg;
    private Double grossWeightInKg;
}