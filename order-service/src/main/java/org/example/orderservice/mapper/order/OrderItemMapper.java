package org.example.orderservice.mapper.order;

import org.example.orderservice.dto.order.OrderItemDto;
import org.example.orderservice.mapper.product.ProductDetailsMapper;
import org.example.orderservice.model.order.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ProductDetailsMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderItemMapper {

    @Mapping(source = "item", target = "product")
    OrderItemDto toDto(OrderItem entity);

    @Mapping(source = "product", target = "item")
    OrderItem toEntity(OrderItemDto entity);
}
