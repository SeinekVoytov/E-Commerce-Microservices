package org.example.userservice.mapper.cart;

import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.product.PriceDto;
import org.example.userservice.model.Cart;
import org.example.userservice.model.CartItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
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

    default Map<String, BigDecimal> computeTotalPrices(Cart cart) {

        Map<String, BigDecimal> totalPrices = new HashMap<>();
        for (CartItem item : cart.getItems()) {
            PriceDto itemPrice = item.getProduct().getPrice();
            BigDecimal totalPrice = itemPrice.amount().multiply(new BigDecimal(item.getQuantity()));
            totalPrices.merge(itemPrice.currency(), totalPrice, BigDecimal::add);
        }

        return totalPrices;
    }

    default Integer computeTotalItems(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getQuantity)
                .reduce(0, Integer::sum);
    }
}