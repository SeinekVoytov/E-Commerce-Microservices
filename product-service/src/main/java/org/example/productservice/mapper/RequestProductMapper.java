package org.example.productservice.mapper;

import org.example.productservice.dto.RequestProductDto;
import org.example.productservice.model.ProductDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ProductDetailsMapper.class}
)
public interface RequestProductMapper {

    @Mapping(source = "images", target = "product.images", ignore = true)
    @Mapping(source = "categoryIds", target = "product.categories", ignore = true)
    ProductDetails toEntity(RequestProductDto dto);
}