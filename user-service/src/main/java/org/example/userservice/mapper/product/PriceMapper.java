package org.example.userservice.mapper.product;

import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.model.product.Price;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PriceMapper {

    PriceDto toDto(Price entity);
}