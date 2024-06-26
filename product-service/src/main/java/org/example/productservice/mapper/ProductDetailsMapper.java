package org.example.productservice.mapper;

import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.model.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {PriceMapper.class, CategoryMapper.class, ProductMapper.class}
)
public interface ProductDetailsMapper {

    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.netWeightInKg", target = "netWeightInKg")
    @Mapping(source = "product.description", target = "description")
    @Mapping(source = "product.images", target = "images")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.categories", target = "categories")
    ProductDetailsDto toDto(ProductDetails entity);

    @Mapping(source = "name", target = "product.name")
    @Mapping(source = "netWeightInKg", target = "product.netWeightInKg")
    @Mapping(source = "description", target = "product.description")
    @Mapping(source = "images", target = "product.images")
    @Mapping(source = "price", target = "product.price")
    @Mapping(source = "categories", target = "product.categories")
    ProductDetails toEntity(ProductDetailsDto entity);
}