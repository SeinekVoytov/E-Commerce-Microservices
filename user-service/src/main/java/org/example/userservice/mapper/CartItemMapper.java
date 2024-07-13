package org.example.userservice.mapper;

import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.model.CartItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.math.BigDecimal;


@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CartItemMapper {

    @Mapping(
            target = "totalPrice",
            expression = "java(computeTotalPrice(entity))"
    )
    CartItemResponse toResponse(CartItem entity);

    List<CartItemResponse> toResponsesList(List<CartItem> entitiesSet);

    default BigDecimal computeTotalPrice(CartItem item) {
        PriceDto itemPrice = item.getProduct().getPrice();
        return itemPrice.amount().multiply(new BigDecimal(item.getQuantity()));
    }
}