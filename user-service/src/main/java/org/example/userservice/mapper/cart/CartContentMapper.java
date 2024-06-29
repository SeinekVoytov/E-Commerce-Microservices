package org.example.userservice.mapper.cart;

import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.Price;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = CartItemMapper.class
)
public interface CartContentMapper {

    @Mapping(
            target = "totalPrices",
            expression = "java(computeTotalPrices(cart))"
    )
    @Mapping(
            target = "totalItems",
            expression = "java(computeTotalItems(cart))"
    )
    CartContentResponse toResponse(Cart cart);

    default Map<Currency, BigDecimal> computeTotalPrices(Cart cart) {

        Map<Currency, BigDecimal> totalPrices = new HashMap<>();
        for (CartItem item : cart.getItems()) {
            Price itemPrice = item.getProduct().getProduct().getPrice();
            BigDecimal totalPrice = itemPrice.getAmount().multiply(new BigDecimal(item.getQuantity()));
            totalPrices.merge(itemPrice.getCurrency(), totalPrice, BigDecimal::add);
        }

        return totalPrices;
    }

    default Integer computeTotalItems(Cart cart) {
        Integer totalItems = 0;
        for (CartItem item : cart.getItems()) {
            totalItems += item.getQuantity();
        }

        return totalItems;
    }
}