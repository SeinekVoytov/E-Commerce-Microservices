package org.example.orderservice.mapper.product;

import org.example.orderservice.dto.product.PriceDto;
import org.example.orderservice.model.product.Price;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PriceMapper {

    PriceDto toDto(Price entity);
}
