package org.example.orderservice.mapper;

import org.example.orderservice.dto.ProductLongDto;
import org.example.orderservice.model.product.ProductLong;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ProductShortMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductLongMapper {

    @Mapping(source = "productShort.name", target = "name")
    @Mapping(source = "productShort.images", target = "images")
    @Mapping(source = "productShort.price", target = "price")
    @Mapping(source = "productShort.categories", target = "categories")
    ProductLongDto toDto(ProductLong entity);

    @Mapping(source = "name", target = "productShort.name")
    @Mapping(source = "images", target = "productShort.images")
    @Mapping(source = "price", target = "productShort.price")
    @Mapping(source = "categories", target = "productShort.categories")
    ProductLong toEntity(ProductLongDto entity);
}
