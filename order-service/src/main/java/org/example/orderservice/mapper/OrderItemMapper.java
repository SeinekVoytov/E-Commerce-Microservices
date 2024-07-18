package org.example.orderservice.mapper;

import org.example.orderservice.dto.cart.CartItemResponse;
import org.example.orderservice.dto.order.OrderItemDto;
import org.example.orderservice.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderItemMapper {

//    @Mapping(source = "id", target = "product.id")
//    OrderItemDto toDto(OrderItem entity);

//    @Mapping(source = "product.id", target = "itemId")
//    OrderItem toEntity(OrderItemDto entity);

    @Mapping(source = "product.id", target = "itemId")
    @Mapping(source = "id", target = "id", ignore = true)
    OrderItem toEntityFromCartItem(CartItemResponse cartItemResponse);
}
