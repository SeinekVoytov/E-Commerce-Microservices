package org.example.orderservice.mapper;

import org.example.orderservice.dto.cart.CartContentResponse;
import org.example.orderservice.dto.order.OrderDetailsResponse;
import org.example.orderservice.model.OrderDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderDetailsMapper {

    @Mapping(source = "order.userId", target = "userId")
    @Mapping(source = "order.delivery.pickUpPoint.id", target = "delivery.pickUpPointId")
    @Mapping(source = "order.delivery.pickUpPoint.address", target = "delivery.pickUpPointAddress")
    @Mapping(source = "order.delivery.status", target = "delivery.deliveryStatus")
    OrderDetailsResponse toDto(OrderDetails entity);

    @Mapping(source = "entity.order.userId", target = "userId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.id", target = "delivery.pickUpPointId")
    @Mapping(source = "entity.order.delivery.pickUpPoint.address", target = "delivery.pickUpPointAddress")
    @Mapping(source = "entity.order.delivery.status", target = "delivery.deliveryStatus")
    @Mapping(source = "cartContent.totalItems", target = "totalItems")
    @Mapping(source = "cartContent.totalPrices", target = "totalPrices")
    OrderDetailsResponse toDtoFromCartContentAndEntity(CartContentResponse cartContent,
                                                       OrderDetails entity);

//    @Mapping(source = "userId", target = "order.userId")
//    OrderDetails toEntity(PickUpPoint pickUpPoint, UUID userId);
}