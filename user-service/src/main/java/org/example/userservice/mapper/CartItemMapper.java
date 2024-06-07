package org.example.userservice.mapper;

import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.model.cart.CartItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = ProductLongMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem entity);
}
