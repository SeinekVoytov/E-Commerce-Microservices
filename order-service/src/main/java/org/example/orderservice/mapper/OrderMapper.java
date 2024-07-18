package org.example.orderservice.mapper;

import org.example.orderservice.dto.order.OrderItemDto;
import org.example.orderservice.dto.order.OrderResponse;
import org.example.orderservice.dto.product.PriceDto;
import org.example.orderservice.dto.product.ProductDetailsDto;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {

    @Mapping(source = "entity.delivery.status", target = "status")
    @Mapping(target = "items", expression = "java(mapOrderItems(entity, products))")
    @Mapping(target = "totalPrices", expression = "java(computeTotalPrices(entity, products))")
    OrderResponse toDto(Order entity, Collection<ProductDetailsDto> products);

    default Set<OrderItemDto> mapOrderItems(Order entity,
                                            Collection<ProductDetailsDto> products) {

        Set<OrderItemDto> orderItems = new HashSet<>();

        Map<Integer, ProductDetailsDto> productsMap = products.stream()
                .collect(Collectors.toMap(ProductDetailsDto::id, p -> p));

        for (OrderItem orderItem : entity.getItems()) {
            ProductDetailsDto product = productsMap.get(orderItem.getItemId());
            orderItems.add(new OrderItemDto(product, orderItem.getQuantity()));
        }

        return orderItems;
    }

    default Map<String, BigDecimal> computeTotalPrices(Order entity,
                                                       Collection<ProductDetailsDto> products) {

        Map<Integer, ProductDetailsDto> productsMap = products.stream()
                .collect(Collectors.toMap(ProductDetailsDto::id, p -> p));

        Map<String, BigDecimal> totalPrices = new HashMap<>();
        for (OrderItem item : entity.getItems()) {
            PriceDto itemPrice = productsMap.get(item.getItemId()).price();
            BigDecimal totalPrice = itemPrice.amount().multiply(new BigDecimal(item.getQuantity()));
            totalPrices.merge(itemPrice.currency().getCurrencyCode(), totalPrice, BigDecimal::add);
        }

        return totalPrices;
    }
}
