package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.model.order.OrderDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderDetailsMapper {

    @Mapping(source = "order.userId", target = "userId")
    @Mapping(source = "order.delivery", target = "delivery")
    @Mapping(source = "order.items", target = "items")
    OrderDetailsDto toDto(OrderDetails entity);

    OrderDetails toEntity(OrderDetailsDto dto);
}
