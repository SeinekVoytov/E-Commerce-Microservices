package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderShortDto;
import org.example.orderservice.model.order.OrderShort;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemShortMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderShortMapper {

    OrderShortDto toDto(OrderShort entity);
    OrderShort toEntity(OrderShortDto dto);
}
