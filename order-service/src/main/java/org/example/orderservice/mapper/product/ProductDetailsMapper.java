package org.example.orderservice.mapper.product;

import org.example.orderservice.dto.product.ProductDetailsDto;
import org.example.orderservice.model.product.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProductMapper.class
)
public interface ProductDetailsMapper {

    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.netWeightInKg", target = "netWeightInKg")
    @Mapping(source = "product.description", target = "description")
    @Mapping(source = "product.brand.name", target = "brand")
    @Mapping(source = "product.images", target = "images")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.categories", target = "categories")
    @Mapping(source = "product.countryManufacturer.name", target = "countryManufacturer")
    ProductDetailsDto toDto(ProductDetails entity);

    @Mapping(source = "name", target = "product.name")
    @Mapping(source = "netWeightInKg", target = "product.netWeightInKg")
    @Mapping(source = "description", target = "product.description")
    @Mapping(source = "brand", target = "product.brand.name")
    @Mapping(source = "images", target = "product.images")
    @Mapping(source = "price", target = "product.price")
    @Mapping(source = "categories", target = "product.categories")
    @Mapping(source = "countryManufacturer", target = "product.countryManufacturer.name")
    ProductDetails toEntity(ProductDetailsDto entity);
}
