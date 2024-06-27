package org.example.productservice.mapper;

import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.model.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RequestProductMapper {

    @Mapping(source = "images", target = "product.images", ignore = true)
    @Mapping(source = "categoryIds", target = "product.categories", ignore = true)
    @Mapping(source = "name", target = "product.name")
    @Mapping(source = "description", target = "product.description")
    @Mapping(source = "netWeightInKg", target = "product.netWeightInKg")
    @Mapping(source = "priceAmount", target = "product.price.amount")
    @Mapping(source = "priceCurrency", target = "product.price.currency")
    ProductDetails toEntity(RequestProductDto dto);
}