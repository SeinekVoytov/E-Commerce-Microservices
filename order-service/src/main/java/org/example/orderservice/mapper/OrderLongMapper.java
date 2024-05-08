package org.example.orderservice.mapper;

import org.example.orderservice.dto.OrderLongDto;
import org.example.orderservice.model.order.OrderLong;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemLongMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderLongMapper {

    OrderLongDto toDto(OrderLong entity);
    OrderLong toEntity(OrderLongDto dto);
}
