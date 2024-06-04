package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.model.order.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {

    @Mapping(source = "delivery.status", target = "status")
    OrderDto toDto(Order entity);
    Order toEntity(OrderDto dto);

    List<OrderDto> listToDtos(List<Order> list);

    List<Order> listToEntities(List<OrderDto> list);
}
