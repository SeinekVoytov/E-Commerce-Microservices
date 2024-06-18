package org.example.userservice.mapper.order;

import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.order.OrderRequest;
import org.example.userservice.dto.order.OrderResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderResponseMapper {

    @Mapping(source = "orderRequest.deliveryType", target = "deliveryType")
    OrderResponse mapToOrderResponse(OrderRequest orderRequest, CartContentResponse orderContent);
}