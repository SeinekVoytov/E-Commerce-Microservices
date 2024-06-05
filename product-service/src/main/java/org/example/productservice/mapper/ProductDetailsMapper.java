package org.example.productservice.mapper;

import org.example.productservice.dto.ProductDetailsDto;
import org.example.productservice.model.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = ProductMapper.class
)
public interface ProductDetailsMapper {

    @Mapping(source = "productShort.name", target = "name")
    @Mapping(source = "productShort.images", target = "images")
    @Mapping(source = "productShort.price", target = "price")
    @Mapping(source = "productShort.categories", target = "categories")
    ProductDetailsDto toDto(ProductDetails entity);

    @Mapping(source = "name", target = "productShort.name")
    @Mapping(source = "images", target = "productShort.images")
    @Mapping(source = "price", target = "productShort.price")
    @Mapping(source = "categories", target = "productShort.categories")
    ProductDetails toEntity(ProductDetailsDto entity);
}
