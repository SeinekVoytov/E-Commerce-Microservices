package org.example.userservice.mapper.cart;

import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.mapper.product.ProductDetailsMapper;
import org.example.userservice.model.cart.CartItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = ProductDetailsMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem entity);

    List<CartItemResponse> toResponsesList(List<CartItem> entitiesSet);
}