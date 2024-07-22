package org.example.orderservice.mapper;

import org.example.orderservice.dto.cart.CartContentResponse;
import org.example.orderservice.dto.order.OrderDetailsResponse;
import org.example.orderservice.dto.product.ProductDetailsDto;
import org.example.orderservice.model.OrderDetails;
import org.example.orderservice.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@Mapper(
        componentModel = "spring",
        uses = {OrderMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class OrderDetailsMapper {

    protected OrderMapper orderMapper;

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Mapping(source = "entity.order.userId", target = "userId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.id", target = "delivery.pickUpPointId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.address", target = "delivery.pickUpPointAddress")
    @Mapping(source = "entity.order.delivery.status", target = "delivery.deliveryStatus")
    @Mapping(target = "items", expression = "java(orderMapper.mapOrderItems(entity.getOrder(), products))")
    @Mapping(target = "totalPrices", expression = "java(orderMapper.computeTotalPrices(entity.getOrder(), products))")
    @Mapping(target = "totalItems", expression = "java(computeTotalItems(entity))")
    public abstract OrderDetailsResponse toDto(OrderDetails entity, Collection<ProductDetailsDto> products);

    @Mapping(source = "entity.order.userId", target = "userId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.id", target = "delivery.pickUpPointId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.address", target = "delivery.pickUpPointAddress")
    @Mapping(source = "entity.order.delivery.status", target = "delivery.deliveryStatus")
    @Mapping(source = "cartContent.totalItems", target = "totalItems")
    @Mapping(source = "cartContent.totalPrices", target = "totalPrices")
    public abstract OrderDetailsResponse toDtoFromCartContentAndEntity(CartContentResponse cartContent,
                                                       OrderDetails entity);

    protected Integer computeTotalItems(OrderDetails entity) {
        return entity.getOrder().getItems().stream()
                .map(OrderItem::getQuantity)
                .reduce(0, Integer::sum);
    }
}