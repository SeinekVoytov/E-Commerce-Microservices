package org.example.orderservice.mapper;

import org.example.orderservice.dto.OrderItemLongDto;
import org.example.orderservice.model.order.OrderItemQuantity;
import org.example.orderservice.model.order.OrderItemLong;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ProductLongMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderItemLongMapper {

    @Mapping(source = "item", target = "product")
    OrderItemLongDto toDto(OrderItemLong entity);

    @Mapping(source = "product", target = "item")
    OrderItemLong toEntity(OrderItemLongDto entity);

    default int quantityWrapperToInt(OrderItemQuantity quantity) {
        return quantity.getQuantity();
    }

    default OrderItemQuantity intToQuantityWrapper(int quantity) {
        OrderItemQuantity orderItemQuantity = new OrderItemQuantity();
        orderItemQuantity.setQuantity(quantity);
        return orderItemQuantity;
    }
}
