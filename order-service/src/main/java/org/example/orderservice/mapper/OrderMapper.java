package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderResponse;
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
    OrderResponse toDto(Order entity);

    Set<OrderResponse> setToDtos(Set<Order> entities);
}
