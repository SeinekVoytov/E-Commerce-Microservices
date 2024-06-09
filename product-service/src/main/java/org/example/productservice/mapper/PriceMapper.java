package org.example.productservice.mapper;

import org.example.productservice.dto.PriceDto;
import org.example.productservice.model.Price;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PriceMapper {

    PriceDto toDto(Price entity);

    Price toEntity(PriceDto dto);
}