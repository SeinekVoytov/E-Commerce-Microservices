package org.example.userservice.mapper.cart;

import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.mapper.product.ProductDetailsMapper;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.Price;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        uses = ProductDetailsMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CartItemMapper {

    @Mapping(
            target = "totalPrice",
            expression = "java(computeTotalPrice(entity))"
    )
    CartItemResponse toResponse(CartItem entity);

    Set<CartItemResponse> toResponsesSet(Set<CartItem> entitiesSet);

    default BigDecimal computeTotalPrice(CartItem item) {
        Price itemPrice = item.getProduct().getProduct().getPrice();
        return itemPrice.getAmount().multiply(new BigDecimal(item.getQuantity()));
    }
}