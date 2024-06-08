package org.example.orderservice.mapper.order;

import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.model.order.Order;
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
    OrderDto toDto(Order entity);

    Set<OrderDto> setToDtos(Set<Order> entities);
}
