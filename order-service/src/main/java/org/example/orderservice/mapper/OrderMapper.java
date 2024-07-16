package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.ResponseOrderDto;
import org.example.orderservice.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {

    @Mapping(source = "delivery.status", target = "status")
    ResponseOrderDto toDto(Order entity);

    Set<ResponseOrderDto> setToDtos(Set<Order> entities);
}
