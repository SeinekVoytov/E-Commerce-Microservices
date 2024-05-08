package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderItemShortDto;
import org.example.orderservice.model.order.OrderItemQuantity;
import org.example.orderservice.model.order.OrderItemShort;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ProductShortMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderItemShortMapper {

    @Mapping(source = "item", target = "product")
    OrderItemShortDto toDto(OrderItemShort entity);

    @Mapping(source = "product", target = "item")
    OrderItemShort toEntity(OrderItemShortDto entity);

    default int quantityWrapperToInt(OrderItemQuantity quantity) {
        return quantity.getQuantity();
    }

    default OrderItemQuantity intToQuantityWrapper(int quantity) {
        OrderItemQuantity orderItemQuantity = new OrderItemQuantity();
        orderItemQuantity.setQuantity(quantity);
        return orderItemQuantity;
    }
}
